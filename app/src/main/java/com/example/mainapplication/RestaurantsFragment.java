package com.example.mainapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsFragment extends Fragment {
    private RecyclerView rvRestaurants;
    private RestaurantAdapter restaurantAdapter;
    private View view;
    public static RestaurantsFragment newInstance() {
        return new RestaurantsFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurants, container, false);

        rvRestaurants = view.findViewById(R.id.rvRestaurants);

        // Initialize adapter with an anonymous click listener
        restaurantAdapter = new RestaurantAdapter(new RestaurantAdapter.OnRestaurantClickListener() {
            @Override
            public void onRestaurantClick(Restaurant restaurant) {
                // Handle restaurant click here
                Toast.makeText(getContext(), "Selected: " + restaurant.getName(), Toast.LENGTH_SHORT).show();

                // You can also start a new activity or fragment here
                // Intent intent = new Intent(getContext(), RestaurantDetailActivity.class);
                // intent.putExtra("restaurant_id", restaurant.getId());
                // startActivity(intent);
            }
        });

        setupRecyclerView();
        loadRestaurants();

        return view;
    }

    private void setupRecyclerView() {
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRestaurants.setAdapter(restaurantAdapter);
    }

    private void loadRestaurants() {
        // Load restaurants from server or local data
        List<Restaurant> restaurants = new ArrayList<>();
        // Add your restaurant loading logic here
        restaurantAdapter.setRestaurants(restaurants);
    }
}