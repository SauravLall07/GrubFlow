package com.example.mainapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private TextView tvOrderHistory;
    private OrderAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_history);

        rvOrders = findViewById(R.id.rvOrders);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        TextView tvCustomerName = findViewById(R.id.tvCustomerName);

        // Setup RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this);
        rvOrders.setAdapter(adapter);

        // Get customer name
        String customerName = getIntent().getStringExtra("customer_name");
        if (customerName != null) {
            tvCustomerName.setText("Customer: " + customerName);
        }

        // Get user ID
        userId = getIntent().getStringExtra("user_id");
        if (userId != null && !userId.isEmpty()) {
            fetchOrders();
        } else {
            tvOrderHistory.setText("No user ID found");
            tvOrderHistory.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        }
    }

    private void fetchOrders() {
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", userId)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2801261/get_orders.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showError("Failed to load orders.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    if (json.getBoolean("success")) {
                        JSONArray orders = json.getJSONArray("orders");
                        List<Order> orderList = new ArrayList<>();

                        for (int i = 0; i < orders.length(); i++) {
                            JSONObject o = orders.getJSONObject(i);
                            orderList.add(new Order(
                                    o.getString("orderId"),
                                    o.getString("restaurant_name"),
                                    o.getString("item_name"),
                                    o.getInt("quantity"),
                                    o.getString("status"),
                                    o.getBoolean("isPaid"),
                                    o.getString("time")
                            ));
                        }

                        runOnUiThread(() -> {
                            if (orderList.isEmpty()) {
                                tvOrderHistory.setText("No order history available");
                                tvOrderHistory.setVisibility(View.VISIBLE);
                                rvOrders.setVisibility(View.GONE);
                            } else {
                                adapter.setOrders(orderList); // ✅ Proper method to update data
                                tvOrderHistory.setVisibility(View.GONE);
                                rvOrders.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        showError(json.getString("message"));
                    }
                } catch (Exception e) {
                    showError("Error parsing response.");
                }
            }
        });
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            tvOrderHistory.setText(message);
            tvOrderHistory.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        });
    }
}
