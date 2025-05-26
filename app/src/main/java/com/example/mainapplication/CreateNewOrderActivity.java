package com.example.mainapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private EditText etCustomerName;
    private EditText etRestaurantName;
    private EditText etItemName;
    private EditText etTime;
    private NumberPicker numberPickerQty;
    private Button btnSubmitOrder;

    private final OkHttpClient client = new OkHttpClient();
    private static final String CREATE_ORDER_URL =
            "https://lamp.ms.wits.ac.za/home/s2801261/create_order.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_new_order);

        // Initialize views
        etStaffName = findViewById(R.id.etStaffName);
        etCustomerName = findViewById(R.id.etCustomerName);
        etRestaurantName = findViewById(R.id.etRestaurantName);
        etItemName = findViewById(R.id.etItemName);
        etTime = findViewById(R.id.etTime);
        numberPickerQty = findViewById(R.id.numberPickerQty);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);

        // Configure number picker
        numberPickerQty.setMinValue(1);
        numberPickerQty.setMaxValue(100);


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

    private void validateAndSubmitOrder(int userId) {
        String restaurantName = etRestaurantName.getText().toString().trim();
        String itemName = etItemName.getText().toString().trim();
        String customerName = etCustomerName.getText().toString().trim();
        int quantity = numberPickerQty.getValue();

        if (restaurantName.isEmpty()) {
            etRestaurantName.setError("Restaurant name required");
            return;
        }

        if (itemName.isEmpty()) {
            etItemName.setError("Item name required");
            return;
        }

        if (customerName.isEmpty()) {
            etCustomerName.setError("Customer name required");
            return;
        }

        String status = "pending";
        boolean isPaid = false;

        // Build request body
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .add("restaurant_name", restaurantName)
                .add("item_name", itemName)
                .add("quantity", String.valueOf(quantity))
                .add("status", status)
                .add("isPaid", isPaid ? "1" : "0")
                .add("customer_name", customerName)
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
        if (response.contains("success")) {
            fetchRestaurantForStaff(userId);
            etItemName.setText("");
            etCustomerName.setText("");
            numberPickerQty.setValue(1);

            String newTime = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(new Date());
            etTime.setText(newTime);

            Toast.makeText(this,
                    "Order created successfully!",
                    Toast.LENGTH_LONG).show();
        } else if (response.contains("Restaurant not found")) {
            etRestaurantName.setError("Restaurant not found in system");
        } else {
            Toast.makeText(this,
                    "Order failed: " + response,
                    Toast.LENGTH_LONG).show();
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

}


