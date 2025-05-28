package com.example.mainapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsFragment extends Fragment {
    private RecyclerView rvRestaurants;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RestaurantAdapter restaurantAdapter;
    private View view;

    public static RestaurantsFragment newInstance() {
        return new RestaurantsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurants, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvRestaurants = view.findViewById(R.id.rvRestaurants);

        // Initialize adapter with an anonymous click listener
        restaurantAdapter = new RestaurantAdapter(new RestaurantAdapter.OnRestaurantClickListener() {
            @Override
            public void onRestaurantClick(Restaurant restaurant) {
                Toast.makeText(getContext(), "Selected: " + restaurant.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshRestaurants();
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
        String url = "https://lamp.ms.wits.ac.za/home/s2801261/get_restaurants.php";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<Restaurant> restaurants = new ArrayList<>();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            String location = obj.getString("location");
                            String contact = obj.getString("contact");
                            double aveRating = obj.getDouble("ave_rating");
                            int numRatings = obj.getInt("num_of_ratings");
                            String imageUrl = obj.getString("image_url");

                            Restaurant restaurant = new Restaurant(
                                    id,
                                    name,
                                    "Great food at great prices",
                                    imageUrl,
                                    aveRating,
                                    numRatings,
                                    location,
                                    contact,
                                    "9:00 AM - 9:00 PM",
                                    true,
                                    "1.2 km"
                            );

                            restaurants.add(restaurant);
                        }

                        // Update UI on main thread
                        requireActivity().runOnUiThread(() -> {
                            restaurantAdapter.setRestaurants(restaurants);
                            updateEmptyView(restaurants);
                            swipeRefreshLayout.setRefreshing(false);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error parsing restaurant data", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        });
                    }
                },
                error -> {
                    error.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load restaurants", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
        );

        queue.add(jsonArrayRequest);
    }

    private void updateEmptyView(List<Restaurant> restaurants) {
        TextView tvNoRestaurants = view.findViewById(R.id.tvNoRestaurants);
        if (restaurants.isEmpty()) {
            tvNoRestaurants.setVisibility(View.VISIBLE);
            rvRestaurants.setVisibility(View.GONE);
        } else {
            tvNoRestaurants.setVisibility(View.GONE);
            rvRestaurants.setVisibility(View.VISIBLE);
        }
    }

    private void refreshRestaurants() {
        swipeRefreshLayout.setRefreshing(true);
        loadRestaurants();
    }
}