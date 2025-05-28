package com.example.mainapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
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

public class OrderHistoryFragment extends Fragment {

    private RecyclerView rvOrders;
    private TextView tvNoOrders;
    private TextView tvCustomerName;
    private OrderAdapter orderAdapter;

    private final OkHttpClient client = new OkHttpClient();
    private String userId;
    private String customerName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use getActivity().getSharedPreferences(...) safely with Context.MODE_PRIVATE
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // To avoid ClassCastException, check if user_id is stored as String or int and read accordingly
        if (prefs.contains("user_id")) {
            try {
                // Try getString first
                userId = prefs.getString("user_id", null);
            } catch (ClassCastException e) {
                // If ClassCastException, try getInt and convert to string
                int userIdInt = prefs.getInt("user_id", -1);
                userId = userIdInt == -1 ? null : String.valueOf(userIdInt);
            }
        }

        // For customerName just read as string (default "Guest")
        customerName = prefs.getString("customer_name", "Guest");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        tvNoOrders = view.findViewById(R.id.tvOrderHistory);
        tvCustomerName = view.findViewById(R.id.tvCustomerName);

        tvCustomerName.setText("Customer: " + customerName);

        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderAdapter = new OrderAdapter(requireContext());
        rvOrders.setAdapter(orderAdapter);

        if (userId != null && !userId.isEmpty()) {
            fetchOrders();
        } else {
            showError("No user ID found.");
        }

        return view;
    }

    private void fetchOrders() {
        RequestBody formBody = new FormBody.Builder()
                .add("customer_id", userId)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2801261/testcustomerorders.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showError("Failed to load orders.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    showError("Server error: " + response.code());
                    response.close();
                    return;
                }

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

                        requireActivity().runOnUiThread(() -> {
                            if (orderList.isEmpty()) {
                                showError("No order history available.");
                            } else {
                                orderAdapter.setOrders(orderList);
                                tvNoOrders.setVisibility(View.GONE);
                                rvOrders.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            showError(json.optString("message", "No order history."));
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        showError("Error parsing order data.");
                    });
                }
            }
        });
    }

    private void showError(String message) {
        requireActivity().runOnUiThread(() -> {
            tvNoOrders.setText(message);
            tvNoOrders.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        });
    }
}
