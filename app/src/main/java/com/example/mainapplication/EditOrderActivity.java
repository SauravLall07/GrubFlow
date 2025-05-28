package com.example.mainapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        tvOrderHistory = findViewById(R.id.tvOrderHistory);  // ✅ Corrected ID
        rvOrders = findViewById(R.id.rvOrders);

        // Get customer name from intent
        String customerName = getIntent().getStringExtra("customer_name");
        if (customerName == null) customerName = "Unknown Customer";
        tvCustomerName.setText("Customer: " + customerName);

        // Setup RecyclerView and Adapter
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this);
        rvOrders.setAdapter(orderAdapter);

        // Load orders
        List<Order> orders = fetchOrdersForCustomer(customerName);
        if (orders.isEmpty()) {
            tvOrderHistory.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvOrderHistory.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            orderAdapter.setOrders(orders);
        }
    }

    // Dummy method - replace with actual data fetching logic
    private List<Order> fetchOrdersForCustomer(String name) {
        // Return empty list simulating no orders for now
        return java.util.Collections.emptyList();
    }
}
