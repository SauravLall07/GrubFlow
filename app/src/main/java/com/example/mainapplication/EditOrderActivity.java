package com.example.mainapplication;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Initialize views
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        rvOrders = findViewById(R.id.rvOrders);

        // Get customer name from intent
        String customerName = getIntent().getStringExtra("customer_name");
        tvCustomerName.setText("Customer: " + customerName);

        // Dummy example: Replace with real order loading
        List<Order> orders = fetchOrdersForCustomer(customerName);

        if (orders.isEmpty()) {
            tvOrderHistory.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvOrderHistory.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);

            rvOrders.setLayoutManager(new LinearLayoutManager(this));
            rvOrders.setAdapter(new OrderAdapter(this, orders));
        }
    }

    // Replace this with a real API or DB query
    private List<Order> fetchOrdersForCustomer(String name) {
        // return emptyList() to simulate "no orders"
        // return Arrays.asList("Pizza - Large", "Burger - Medium") for fake orders
        return java.util.Collections.emptyList();
    }
}
