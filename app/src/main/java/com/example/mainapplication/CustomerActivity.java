package com.example.mainapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.rvOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderAdapter(this, orderList);
        recyclerView.setAdapter(adapter);

        // Replace this with your API call
        mockLoadOrders();
    }

    private void mockLoadOrders() {
        orderList.add(new Order("101", "Burger House", "Pending", false));
        orderList.add(new Order("102", "Sushi Corner", "Ready", true));
        orderList.add(new Order("103", "Pizza Palace", "Collected", false));
        orderList.add(new Order("104", "McDonalds", "Ready", false));
        orderList.add(new Order("105", "KFC", "Pending", false));
        orderList.add(new Order("106", "Nandos", "Collected", true));
        adapter.notifyDataSetChanged();
    }
}