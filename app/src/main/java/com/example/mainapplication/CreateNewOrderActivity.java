package com.example.mainapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateNewOrderActivity extends AppCompatActivity {

    private EditText etStaffName;
    private AutoCompleteTextView etCustomerName;
    private EditText etRestaurantName;
    private EditText etItemName;
    private EditText etTime;
    private NumberPicker numberPickerQty;
    private Button btnSubmitOrder;
    private RecyclerView userRecyclerView;
    private EditText userSearchInput;
    private UserAdapter userAdapter;
    private int selectedUserId = -1;
    private String selectedUserName = "";
    private boolean isDropdownVisible = false;
    private int selectedCustomerId = -1;
    private final OkHttpClient client = new OkHttpClient();
    private static final String CREATE_ORDER_URL =
            "https://lamp.ms.wits.ac.za/home/s2801261/create_order.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_new_order);

        // Initialize views
        etStaffName = findViewById(R.id.etStaffName);
        etRestaurantName = findViewById(R.id.etRestaurantName);
        etItemName = findViewById(R.id.etItemName);
        etTime = findViewById(R.id.etTime);
        numberPickerQty = findViewById(R.id.numberPickerQty);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);
        userRecyclerView = findViewById(R.id.userList);
        userSearchInput = findViewById(R.id.userSearchInput);

        // Initialize RecyclerView
        userAdapter = new UserAdapter(user -> {
            selectedUserId = user.getId();
            selectedUserName = user.getName();
            userSearchInput.setText(selectedUserName);
            hideDropdown();
        });
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userAdapter);

        // Configure number picker
        numberPickerQty.setMinValue(1);
        numberPickerQty.setMaxValue(100);

        // Search functionality
        userSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    hideDropdown();
                    return;
                }

                searchUsers(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Get logged-in user info
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        final int userId = prefs.getInt("user_id", -1);
        String staffName = prefs.getString("staff_name", "");

        // Set staff name and time
        etStaffName.setText(staffName);
        fetchRestaurantForStaff(userId);
        String currentTime = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(new Date());
        etTime.setText(currentTime);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnSubmitOrder.setOnClickListener(v -> validateAndSubmitOrder(userId));
    }

    private void searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            hideDropdown();
            return;
        }

        new Thread(() -> {
            try {
                String urlString = "https://lamp.ms.wits.ac.za/home/s2801261/search_customers.php";
                URL url = new URL(urlString + "?query=" + URLEncoder.encode(query, "UTF-8"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                List<User> users = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject userJson = jsonArray.getJSONObject(i);
                    int id = userJson.getInt("id");
                    String name = userJson.getString("name");

                    // Try to get email, but handle if it's missing
                    String email = null;
                    if (userJson.has("email")) {
                        email = userJson.getString("email");
                    }

                    User user = new User(id, name, email);
                    users.add(user);
                }

                runOnUiThread(() -> {
                    userAdapter.setUsers(users);
                    showDropdown();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void handleNetworkError(String errorMessage) {
        runOnUiThread(() -> {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            hideDropdown();
            if (userSearchInput != null) {
                userSearchInput.setEnabled(true);
            }
        });
    }

    private void showDropdown() {
        if (!isDropdownVisible) {
            userRecyclerView.setVisibility(View.VISIBLE);
            isDropdownVisible = true;
        }
    }

    private void hideDropdown() {
        if (isDropdownVisible) {
            userRecyclerView.setVisibility(View.GONE);
            isDropdownVisible = false;
        }
    }


    private void validateAndSubmitOrder(int userId) {
        String restaurantName = etRestaurantName.getText().toString().trim();
        String itemName = etItemName.getText().toString().trim();
        String customerName = userSearchInput.getText().toString().trim();
        int quantity = numberPickerQty.getValue();

        if (restaurantName.isEmpty()) {
            etRestaurantName.setError("Restaurant name required");
            return;
        }

        if (itemName.isEmpty()) {
            etItemName.setError("Item name required");
            return;
        }

        if (customerName.isEmpty() || selectedUserId == -1) {
            userSearchInput.setError("Please select a customer");
            return;
        }

        String status = "pending";
        boolean isPaid = false;

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .add("customer_id", String.valueOf(selectedUserId))
                .add("restaurant_name", restaurantName)
                .add("item_name", itemName)
                .add("quantity", String.valueOf(quantity))
                .add("status", status)
                .add("isPaid", isPaid ? "1" : "0")
                .build();

        submitOrderToServer(formBody, userId);
    }


    private void submitOrderToServer(RequestBody formBody, int userId) {
        Request request = new Request.Builder()
                .url(CREATE_ORDER_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(CreateNewOrderActivity.this,
                                "Network error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                runOnUiThread(() -> handleServerResponse(responseBody, userId));
            }
        });
    }

    private void handleServerResponse(String response, int userId) {
        // Optional: Log server response
        System.out.println("Server response: " + response);

        if (response.toLowerCase().contains("success")) {
            runOnUiThread(() -> {
                // Reset form
                etItemName.setText("");
                userSearchInput.setText("");
                selectedCustomerId = -1;
                numberPickerQty.setValue(1);
                hideDropdown();

                String newTime = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(new Date());
                etTime.setText(newTime);

                Toast.makeText(CreateNewOrderActivity.this,
                        "Order created successfully!",
                        Toast.LENGTH_LONG).show();

                // Go back to previous screen (e.g., Staff Menu)
                finish();  // Close this activity
            });

        } else if (response.contains("Restaurant not found")) {
            runOnUiThread(() -> etRestaurantName.setError("Restaurant not found in system"));

        } else {
            runOnUiThread(() -> Toast.makeText(this,
                    "Order failed: " + response,
                    Toast.LENGTH_LONG).show());
        }
    }


    private void fetchRestaurantForStaff(int userId) {
        RequestBody body = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2801261/get_staff_restaurant.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CreateNewOrderActivity.this,
                        "Failed to load restaurant info",
                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();

                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(responseData);
                        if (json.getBoolean("success")) {
                            String restaurant = json.getString("restaurant");
                            etRestaurantName.setText(restaurant);
                            etRestaurantName.setEnabled(false); // optional: lock it from editing
                        } else {
                            Toast.makeText(CreateNewOrderActivity.this,
                                    "No restaurant info found for staff.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(CreateNewOrderActivity.this,
                                "Error parsing restaurant info",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    class Customer {
        int id;
        String name;

        Customer(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name; // AutoCompleteTextView will display name
        }
    }


}


