package com.example.mainapplication;

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

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {
    private RecyclerView rvOrders;
    private TextView tvNoOrders;
    private String customerName;
    private String ordersJson;
    private OrderAdapter orderAdapter;

    public static OrderHistoryFragment newInstance(String customerName, String ordersJson) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        Bundle args = new Bundle();
        args.putString("customer_name", customerName);
        args.putString("orders_json", ordersJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
        customerName = prefs.getString("customer_name", "Guest");

        if (getArguments() != null) {
            ordersJson = getArguments().getString("orders_json", "[]"); // Default to empty array
            // If provided, override customerName with argument
            customerName = getArguments().getString("customer_name", customerName);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        tvNoOrders = view.findViewById(R.id.tvOrderHistory);
        TextView tvCustomerName = view.findViewById(R.id.tvCustomerName);

        tvCustomerName.setText("Customer: " + customerName);

        setupRecyclerView();
        loadOrders();

        return view;
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderAdapter = new OrderAdapter(requireContext());
        rvOrders.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        try {
            JSONArray orders = new JSONArray(ordersJson);
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

            if (orderList.isEmpty()) {
                rvOrders.setVisibility(View.GONE);
                tvNoOrders.setText("No order history available.");
                tvNoOrders.setVisibility(View.VISIBLE);
            } else {
                orderAdapter.setOrders(orderList);
                rvOrders.setVisibility(View.VISIBLE);
                tvNoOrders.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            rvOrders.setVisibility(View.GONE);
            tvNoOrders.setText("Error loading order history.");
            tvNoOrders.setVisibility(View.VISIBLE);
        }
    }
}
