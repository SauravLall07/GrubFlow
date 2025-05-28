package com.example.mainapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private List<Restaurant> restaurants;
    private OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public RestaurantAdapter(OnRestaurantClickListener listener) {
        this.restaurants = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvRating;
        private TextView tvReviewCount;
        private TextView tvDistance;
        private TextView tvOpeningStatus;
        private TextView tvCellNumber;
        private TextView tvLocation;
        private ImageView ivRestaurant;
        private View container;

        RestaurantViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRestaurantName);
            tvCellNumber = itemView.findViewById(R.id.tvCellNumber);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDescription = itemView.findViewById(R.id.tvRestaurantDescription);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvOpeningStatus = itemView.findViewById(R.id.tvOpeningStatus);
            ivRestaurant = itemView.findViewById(R.id.ivRestaurant);
            container = itemView.findViewById(R.id.container);

            container.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRestaurantClick(restaurants.get(getAdapterPosition()));
                }
            });
        }

        void bind(Restaurant restaurant) {
            tvName.setText(restaurant.getName());
            tvCellNumber.setText(restaurant.getContact());
            tvLocation.setText(restaurant.getLocation());
            tvDescription.setText(restaurant.getDescription());

            // Format rating and review count
            tvRating.setText(String.format("%.1f", restaurant.getAveRating()));
            tvReviewCount.setText(String.format("(%d reviews)", restaurant.getNumRatings()));

            // Format distance
            tvDistance.setText(restaurant.getDistance());

            Glide.with(itemView.getContext())
                    .load(restaurant.getImageUrl())
                    .centerCrop()
                    .into(ivRestaurant);

            // Set opening status
            if (restaurant.isOpen()) {
                tvOpeningStatus.setText("Open Now");
                tvOpeningStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
            } else {
                tvOpeningStatus.setText("Closed");
                tvOpeningStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
            }


        }
    }
}