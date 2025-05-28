package com.example.mainapplication;

import android.os.Bundle;
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
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_history);

        // Initialize views
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        rvOrders = findViewById(R.id.rvOrders);

        // Get customer name and orders JSON from intent
        String customerName = getIntent().getStringExtra("customer_name");
        String ordersJson = getIntent().getStringExtra("orders_json");

        if (customerName == null) customerName = "Unknown Customer";
        tvCustomerName.setText("Customer: " + customerName);

        // Setup RecyclerView and Adapter
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this);
        rvOrders.setAdapter(orderAdapter);

        // Parse and display orders
        List<Order> orders = parseOrdersFromJson(ordersJson, customerName);

        if (orders.isEmpty()) {
            tvOrderHistory.setVisibility(View.VISIBLE);
            tvOrderHistory.setText("No orders found.");
            rvOrders.setVisibility(View.GONE);
        } else {
            tvOrderHistory.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            orderAdapter.setOrders(orders);
        }
    }

    private List<Order> parseOrdersFromJson(String json, String customerName) {
        List<Order> orders = new ArrayList<>();
        if (json == null || json.isEmpty()) return orders;

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String orderId = obj.getString("order_id");
                String itemName = obj.getString("item_name");
                int quantity = obj.optInt("quantity", 0);
                String status = obj.getString("status");
                String orderTime = obj.getString("order_time");
                boolean isPaid = obj.optBoolean("is_paid", false);
                boolean isRated = obj.optBoolean("is_rated", false);
                int rating = obj.optInt("rating", 0);

                String details = itemName + " (x" + quantity + ")";
                Order order = new Order(orderId, "Unknown", details, status,
                        isPaid, orderTime, rating, isRated, customerName);
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
}
