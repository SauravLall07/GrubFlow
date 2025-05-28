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
    private TextView tvCustomerName;
    private OrderAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_history); // ✅ Correct layout

        rvOrders = findViewById(R.id.rvOrders);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        tvCustomerName = findViewById(R.id.tvCustomerName);

        // Setup RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this);
        rvOrders.setAdapter(adapter);

        // Get customer name
        String customerName = getIntent().getStringExtra("customer_name");
        if (customerName != null && !customerName.isEmpty()) {
            tvCustomerName.setText("Customer: " + customerName);
        } else {
            tvCustomerName.setText("Customer: Guest");
        }

        // Get user ID
        userId = getIntent().getStringExtra("user_id");
        if (userId != null && !userId.isEmpty()) {
            fetchOrders();
        } else {
            showError("No user ID found");
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
                            JSONObject order = orders.getJSONObject(i);
                            orderList.add(new Order(
                                    order.optString("orderId"),
                                    order.optString("restaurant_name"),
                                    order.optString("item_name"),
                                    order.optInt("quantity"),
                                    order.optString("status"),
                                    order.optBoolean("isPaid"),
                                    order.optString("time")
                            ));
                        }

                        runOnUiThread(() -> {
                            if (orderList.isEmpty()) {
                                showError("No order history available");
                            } else {
                                adapter.setOrders(orderList);
                                tvOrderHistory.setVisibility(View.GONE);
                                rvOrders.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        showError(json.optString("message", "Something went wrong."));
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
