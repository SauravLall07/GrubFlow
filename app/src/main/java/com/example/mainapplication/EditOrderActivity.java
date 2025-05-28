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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_history);

        // Initialize views
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvOrderHistory = findViewById(R.id.rvOrders);
        rvOrders = findViewById(R.id.rvOrders);

        String customerName = getIntent().getStringExtra("customer_name");
        if (customerName == null) customerName = "Unknown Customer";
        tvCustomerName.setText("Customer: " + customerName);

        // Fetch orders for this customer (replace with real API/db call)
        List<Order> orders = fetchOrdersForCustomer(customerName);

        if (orders.isEmpty()) {
            tvOrderHistory.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvOrderHistory.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);

            rvOrders.setLayoutManager(new LinearLayoutManager(this));
            rvOrders.setAdapter(new OrderAdapter(this,orders));
        }
    }

    // Dummy method - replace with your actual data fetching logic
    private List<Order> fetchOrdersForCustomer(String name) {
        // Return empty list simulating no orders for this example
        return java.util.Collections.emptyList();
    }
}
