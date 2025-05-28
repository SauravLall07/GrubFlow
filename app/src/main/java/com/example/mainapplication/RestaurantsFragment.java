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

import org.json.JSONException;
import org.json.JSONObject;

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

                            // Fill the rest with default/placeholder values
                            Restaurant restaurant = new Restaurant(
                                    id,
                                    name,
                                    "Great food at great prices", // description
                                    "", // imageUrl
                                    aveRating,
                                    numRatings, // reviewCount
                                    location, // address
                                    contact, // phoneNumber
                                    "9:00 AM - 9:00 PM", // openingHours
                                    true, // isOpen
                                    "1.2 km" // distance
                            );

                            restaurants.add(restaurant);
                        }

                        restaurantAdapter.setRestaurants(restaurants);

                        // Show/hide empty message
                        TextView tvNoRestaurants = view.findViewById(R.id.tvNoRestaurants);
                        if (restaurants.isEmpty()) {
                            tvNoRestaurants.setVisibility(View.VISIBLE);
                            rvRestaurants.setVisibility(View.GONE);
                        } else {
                            tvNoRestaurants.setVisibility(View.GONE);
                            rvRestaurants.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing restaurant data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load restaurants", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(jsonArrayRequest);
    }

}