package com.example.mainapplication;

import android.app.Activity;
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
        setContentView(R.layout.fragment_order_history);

        rvOrders = findViewById(R.id.rvOrders);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        tvCustomerName = findViewById(R.id.tvCustomerName);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this);
        rvOrders.setAdapter(adapter);

        String customerName = getIntent().getStringExtra("customer_name");
        if (customerName != null && !customerName.isEmpty()) {
            tvCustomerName.setText("Customer: " + customerName);
        } else {
            tvCustomerName.setText("Customer: Guest");
        }

        userId = getIntent().getStringExtra("user_id");
        if (userId != null && !userId.isEmpty()) {
            fetchOrders();
        } else {
            showError("No user ID found");
        }
    }

    private void fetchOrders() {
        // FIXED: The PHP expects 'customer_id', not 'user_id'
        RequestBody formBody = new FormBody.Builder()
                .add("customer_id", userId)  // corrected key to 'customer_id'
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
                response.close();

                try {
                    JSONObject json = new JSONObject(responseData);

                    if (json.getBoolean("success")) {
                        JSONArray orders = json.getJSONArray("orders");
                        List<Order> orderList = new ArrayList<>();

                        for (int i = 0; i < orders.length(); i++) {
                            JSONObject order = orders.getJSONObject(i);
                            orderList.add(new Order(
                                    order.getString("order_id"),
                                    order.getString("restaurant_name"),
                                    order.getString("items"),
                                    order.getString("status"),
                                    order.getBoolean("isPaid"),
                                    order.getString("order_date"),
                                    order.optInt("rating", -1),
                                    order.optInt("isRated", 0) == 1,
                                    order.optString("customer_name", "Guest")
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
                        runOnUiThread(() -> {
                            showError(json.optString("message", "Something went wrong."));
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        showError("Error parsing response.");
                    });
                }
            }

        });
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            tvOrderHistory.setText(message);
            tvOrderHistory.setVisibility(TextView.VISIBLE);
            rvOrders.setVisibility(RecyclerView.GONE);
        });
    }
}
