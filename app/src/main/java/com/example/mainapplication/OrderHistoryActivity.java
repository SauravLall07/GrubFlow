package com.example.mainapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderHistoryActivity extends AppCompatActivity {

    TextView tvOrderHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        String ordersJson = getIntent().getStringExtra("orders_json");

        try {
            JSONArray orders = new JSONArray(ordersJson);
            if (orders.length() == 0) {
                tvOrderHistory.setText("No order history.");
            } else {
                StringBuilder history = new StringBuilder();
                for (int i = 0; i < orders.length(); i++) {
                    JSONObject order = orders.getJSONObject(i);
                    history.append("Order #").append(order.getInt("id"))
                            .append(" - ").append(order.getString("details"))
                            .append("\n\n");
                }
                tvOrderHistory.setText(history.toString());
            }
        } catch (JSONException e) {
            tvOrderHistory.setText("Error parsing orders.");
        }
    }
}

