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
    private final List<Order> orderList = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_history);

        rvOrders = findViewById(R.id.rvOrders);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        TextView tvCustomerName = findViewById(R.id.tvCustomerName);

        // Initialize RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, orderList);
        rvOrders.setAdapter(adapter);

        // Get customer name from intent
        String customerName = getIntent().getStringExtra("customer_name");
        if (customerName != null) {
            tvCustomerName.setText("Customer: " + customerName);
        }

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
                runOnUiThread(() -> {
                    tvOrderHistory.setText("Failed to load orders.");
                    rvOrders.setVisibility(View.GONE);
                    tvOrderHistory.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(responseData);
                        if (json.getBoolean("success")) {
                            JSONArray orders = json.getJSONArray("orders");
                            orderList.clear();

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

                            if (orderList.isEmpty()) {
                                tvOrderHistory.setText("No order history available");
                                tvOrderHistory.setVisibility(View.VISIBLE);
                                rvOrders.setVisibility(View.GONE);
                            } else {
                                tvOrderHistory.setVisibility(View.GONE);
                                rvOrders.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            showError(json.getString("message"));
                        }
                    } catch (Exception e) {
                        showError("Error parsing response");
                    }
                });
            }
        });
    }

    private void showError(String msg) {
        runOnUiThread(() -> {
            tvOrderHistory.setText(msg);
            rvOrders.setVisibility(View.GONE);
            tvOrderHistory.setVisibility(View.VISIBLE);
        });
    }
}