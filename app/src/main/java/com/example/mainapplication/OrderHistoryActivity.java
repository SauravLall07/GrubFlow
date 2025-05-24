package com.example.mainapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private TextView tvOrderHistory;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvOrders = findViewById(R.id.rvOrders);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, orderList);
        rvOrders.setAdapter(adapter);

        String ordersJson = getIntent().getStringExtra("orders_json");

        try {
            JSONArray orders = new JSONArray(ordersJson);
            if (orders.length() == 0) {
                rvOrders.setVisibility(View.GONE);
                tvOrderHistory.setVisibility(View.VISIBLE);
                tvOrderHistory.setText("No order history.");
            } else {
                for (int i = 0; i < orders.length(); i++) {
                    JSONObject order = orders.getJSONObject(i);
                    String id = order.getString("id");
                    String details = order.getString("details");
                    String status = order.optString("status", "Pending");
                    boolean isPaid = order.optBoolean("is_paid", false);

                    orderList.add(new Order(id, details, status, isPaid));
                }
                tvOrderHistory.setVisibility(View.GONE);
                rvOrders.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            tvOrderHistory.setText("Error parsing orders.");
            rvOrders.setVisibility(View.GONE);
            tvOrderHistory.setVisibility(View.VISIBLE);
        }
    }
    //stub method
    private String getOrdersJsonForCustomer(String customerName) {
        // For example purposes:
        if (customerName.equalsIgnoreCase("Alice")) {
            // Return a sample JSON array string of orders
            return "[{\"id\":\"123\", \"details\":\"Burger & Fries\", \"status\":\"Delivered\", \"is_paid\":true}," +
                    "{\"id\":\"124\", \"details\":\"Pizza\", \"status\":\"Pending\", \"is_paid\":false}]";
        } else if (customerName.equalsIgnoreCase("Bob")) {
            // Bob has no orders
            return "[]";
        }
        // Customer not found, return null
        return null;
    }
}
