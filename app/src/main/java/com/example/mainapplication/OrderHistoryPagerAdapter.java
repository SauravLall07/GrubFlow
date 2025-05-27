package com.example.mainapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrderHistoryPagerAdapter extends FragmentStateAdapter {
    private final String customerName;
    private final String ordersJson;

    public OrderHistoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, String customerName, String ordersJson) {
        super(fragmentActivity);
        this.customerName = customerName;
        this.ordersJson = ordersJson;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return OrderHistoryFragment.newInstance(customerName, ordersJson);
            case 1:
                return RestaurantsFragment.newInstance();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}