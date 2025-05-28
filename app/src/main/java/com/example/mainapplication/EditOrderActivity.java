package com.example.mainapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditOrderActivity extends AppCompatActivity {

    private TextView tvCustomerName, tvOrderHistory;
    private RecyclerView rvOrders;
    private StaffOrderAdapter staffOrderAdapter;  // fix variable name to camelCase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_history);

        // Initialize UI elements
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        rvOrders = findViewById(R.id.rvOrders);

        // Retrieve data from Intent
        String customerName = getIntent().getStringExtra("customer_name");
        String ordersJson = getIntent().getStringExtra("orders_json");

        Log.d("EditOrderActivity", "Received JSON: " + ordersJson);

        if (customerName == null || customerName.isEmpty()) {
            customerName = "Unknown Customer";
        }

        tvCustomerName.setText("Customer: " + customerName);

        // Setup RecyclerView and Adapter
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        staffOrderAdapter = new StaffOrderAdapter(this);  // instantiate the correct adapter
        rvOrders.setAdapter(staffOrderAdapter);           // set correct adapter

        // Parse and load orders
        List<Order> orders = parseOrdersFromJson(ordersJson, customerName);

        if (orders.isEmpty()) {
            tvOrderHistory.setVisibility(View.VISIBLE);
            tvOrderHistory.setText("No orders found.");
            rvOrders.setVisibility(View.GONE);
        } else {
            tvOrderHistory.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            staffOrderAdapter.setOrders(orders);          // use correct adapter instance here
        }
    }

    private List<Order> parseOrdersFromJson(String json, String customerName) {
        List<Order> orders = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return orders;

        try {
            json = json.trim();
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    orders.add(parseOrderObject(obj, customerName));
                }
            } else if (json.startsWith("{")) {
                JSONObject obj = new JSONObject(json);
                if (obj.has("orders")) {
                    JSONArray jsonArray = obj.getJSONArray("orders");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        orders.add(parseOrderObject(jsonArray.getJSONObject(i), customerName));
                    }
                } else {
                    orders.add(parseOrderObject(obj, customerName));
                }
            } else {
                Log.e("EditOrderActivity", "Unexpected JSON format");
            }
        } catch (Exception e) {
            Log.e("EditOrderActivity", "JSON parsing error: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }

    private Order parseOrderObject(JSONObject obj, String customerName) {
        String orderId = obj.optString("order_id", "");
        String itemName = obj.optString("item_name", "Unknown item");
        int quantity = obj.optInt("quantity", 0);
        String status = obj.optString("status", "Unknown");
        String orderTime = obj.optString("order_time", "");
        boolean isPaid = obj.optBoolean("is_paid", false);
        boolean isRated = obj.optBoolean("is_rated", false);
        int rating = obj.optInt("rating", 0);
        String restaurantName = obj.optString("restaurant_name", "Unknown");

        String details = itemName
                + " (x" + quantity + ")"
                + " @ " + restaurantName;

        return new Order(orderId, restaurantName, details, status,
                isPaid, orderTime, rating, isRated, customerName);
    }
}
