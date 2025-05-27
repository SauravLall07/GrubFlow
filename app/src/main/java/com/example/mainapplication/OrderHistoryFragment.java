package com.example.mainapplication;

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
        if (getArguments() != null) {
            customerName = getArguments().getString("customer_name");
            ordersJson = getArguments().getString("orders_json");
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
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext());
        rvOrders.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        try {
            JSONArray orders = new JSONArray(ordersJson);
            List<Order> orderList = new ArrayList<>();
            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = orders.getJSONObject(i);
                orderList.add(new Order(
                        order.getString("orderId"),
                        order.getString("restaurant_name"),
                        order.getString("item_name"),
                        order.getInt("quantity"),
                        order.getString("status"),
                        order.getBoolean("isPaid"),
                        order.getString("time")
                ));
            }
            orderAdapter.setOrders(orderList);
            rvOrders.setVisibility(View.VISIBLE);
            tvNoOrders.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            rvOrders.setVisibility(View.GONE);
            tvNoOrders.setVisibility(View.VISIBLE);
        }
    }
}