package com.example.mainapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.android.material.navigation.NavigationView;

public class StaffMenuActivity extends AppCompatActivity {

    EditText etCustomerName;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private EditText userSearchInput;
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private int selectedUserId = -1;
    private String selectedUserName = "";

    // Cache last search result for dropdown to show on click
    private List<User> currentUserList = new ArrayList<>();

    // NEW: Flag to detect programmatic text change
    private boolean isProgrammaticTextChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        userSearchInput = findViewById(R.id.userSearchInput);
        userRecyclerView = findViewById(R.id.userList);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                logout();
                return true;
            }
            return false;
        });

        // Adapter callback
        userAdapter = new UserAdapter(user -> {
            selectedUserId = user.getId();
            selectedUserName = user.getName();

            // NEW: Programmatic text change block
            isProgrammaticTextChange = true;
            userSearchInput.setText(selectedUserName);
            isProgrammaticTextChange = false;

            userAdapter.setUsers(new ArrayList<>());
            hideDropdown();

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(userSearchInput.getWindowToken(), 0);
            }
            userSearchInput.clearFocus();
        });

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userAdapter);

        userSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // NEW: Ignore programmatic text changes
                if (isProgrammaticTextChange) return;

                if (s.length() < 1) {
                    hideDropdown();
                    return;
                }
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        userSearchInput.setOnClickListener(v -> {
            if (!selectedUserName.isEmpty() && !isDropdownVisible() && !currentUserList.isEmpty()) {
                userAdapter.setUsers(currentUserList);
                showDropdown();
            }
        });

        Button btnNewOrder = findViewById(R.id.btnNewOrder);
        Button btnEditOrder = findViewById(R.id.btnEditOrder);

        btnNewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(StaffMenuActivity.this, CreateNewOrderActivity.class);
            startActivity(intent);
        });

        btnEditOrder.setOnClickListener(v -> {
            String name = selectedUserName;
            if (!name.isEmpty()) {
                checkCustomerOrders(name);
            } else {
                Toast.makeText(this, "Please select a customer", Toast.LENGTH_SHORT).show();
            }
        });

        View headerView = navigationView.getHeaderView(0);
        TextView tvMemberName = headerView.findViewById(R.id.tvMemberName);
        String memberName = getIntent().getStringExtra("staff_name");
        tvMemberName.setText(memberName != null && !memberName.isEmpty() ? memberName : "Staff");
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
                    String email = userJson.optString("email", null);
                    users.add(new User(id, name, email));
                }

                runOnUiThread(() -> {
                    currentUserList = users;
                    userAdapter.setUsers(users);
                    showDropdown();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void showDropdown() {
        if (userRecyclerView != null && userRecyclerView.getVisibility() == View.GONE) {
            userRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void hideDropdown() {
        if (userRecyclerView != null && userRecyclerView.getVisibility() == View.VISIBLE) {
            userRecyclerView.setVisibility(View.GONE);
        }
    }

    private boolean isDropdownVisible() {
        return userRecyclerView != null && userRecyclerView.getVisibility() == View.VISIBLE;
    }

    private void logout() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void checkCustomerOrders(String name) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("customer_name", name)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2801261/get_order.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(StaffMenuActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String responseString = response.body().string();

                    runOnUiThread(() -> {
                        Log.d("SERVER_RESPONSE", responseString);
                        try {
                            JSONObject json = new JSONObject(responseString);

                            if (!json.getBoolean("success")) {
                                Toast.makeText(StaffMenuActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONArray ordersArray = json.getJSONArray("orders");

                            Intent intent = new Intent(StaffMenuActivity.this, EditOrderActivity.class);
                            intent.putExtra("customer_name", name);
                            intent.putExtra("orders_json", ordersArray.toString());
                            startActivity(intent);

                        } catch (Exception e) {
                            Log.e("RESPONSE_ERROR", "Parsing error", e);
                            Toast.makeText(StaffMenuActivity.this, "Response parsing error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
