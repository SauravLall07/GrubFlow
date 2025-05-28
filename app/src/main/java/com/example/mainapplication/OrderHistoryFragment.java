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
            JSONObject jsonObject = new JSONObject(ordersJson);
            if (jsonObject.getBoolean("success")) {
                JSONArray orders = jsonObject.getJSONArray("orders");
                List<Order> orderList = new ArrayList<>();

                for (int i = 0; i < orders.length(); i++) {
                    JSONObject order = orders.getJSONObject(i);

                    // Extract all fields exactly as returned by PHP
                    String orderId = order.getString("order_id");
                    String orderDate = order.getString("order_date");
                    String status = order.getString("status");
                    boolean isPaid = order.getBoolean("isPaid");
                    int rating = order.optInt("rating", -1);       // use optInt to avoid exceptions
                    boolean isRated = order.optInt("isRated", 0) == 1;  // assuming 1 or 0 from DB
                    String restaurantName = order.getString("restaurant_name");
                    String customerName = order.getString("customer_name");
                    String items = order.getString("items");  // e.g. "Burger (2), Chips (1)"

                    // Pass these to your Order constructor
                    orderList.add(new Order(orderId, restaurantName, items, status, isPaid, orderDate, rating, isRated, customerName));
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

            } else {
                // Handle 'success' == false case
                rvOrders.setVisibility(View.GONE);
                tvNoOrders.setText("No order history found.");
                tvNoOrders.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            rvOrders.setVisibility(View.GONE);
            tvNoOrders.setText("Error loading order history.");
            tvNoOrders.setVisibility(View.VISIBLE);
        }
    }



}
