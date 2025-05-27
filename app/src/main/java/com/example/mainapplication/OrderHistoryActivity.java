package com.example.mainapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainapplication.Order;
import com.example.mainapplication.OrderAdapter;
import com.example.mainapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private TextView tvOrderHistory;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvOrders = findViewById(R.id.rvOrders);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        TextView tvCustomerName = findViewById(R.id.tvCustomerName);

        // Get customer name from intent
        String customerName = getIntent().getStringExtra("customer_name");
        if (customerName != null) {
            tvCustomerName.setText("Customer: " + customerName);
        }


        // Rest of your existing order loading code...
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
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    tvOrderHistory.setText("Failed to load orders.");
                    rvOrders.setVisibility(View.GONE);
                    tvOrderHistory.setVisibility(View.VISIBLE);
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();

                try {
                    JSONObject json = new JSONObject(responseData);
                    if (json.getBoolean("success")) {
                        JSONArray orders = json.getJSONArray("orders");
                        orderList.clear();

                        for (int i = 0; i < orders.length(); i++) {
                            JSONObject o = orders.getJSONObject(i);
                            String id = o.getString("id");
                            String details = o.getString("details");
                            String status = o.optString("status", "Pending");
                            boolean isPaid = o.optBoolean("is_paid", false);
                            boolean isRated = o.optBoolean("is_rated", false);
                            orderList.add(new Order(id, details, status, isPaid, isRated));
                        }

                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            rvOrders.setVisibility(View.VISIBLE);
                            tvOrderHistory.setVisibility(View.GONE);
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

    private void showError(String msg) {
        runOnUiThread(() -> {
            tvOrderHistory.setText(msg);
            rvOrders.setVisibility(View.GONE);
            tvOrderHistory.setVisibility(View.VISIBLE);
        });
    }
}
