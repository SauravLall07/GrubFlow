package com.example.mainapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private Context context;

    public OrderAdapter(Context context) {
        this.context = context;
        this.orderList = new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        this.orderList = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvRestaurant.setText(order.getRestaurantName());
        holder.tvStatus.setText("Status: " + order.getStatus());

        boolean isRated = order.isRated();
        holder.btnThumbUp.setVisibility(isRated ? View.GONE : View.VISIBLE);
        holder.btnThumbDown.setVisibility(isRated ? View.GONE : View.VISIBLE);

        // Optional: Disable buttons immediately to avoid multiple taps
        holder.btnThumbUp.setEnabled(!isRated);
        holder.btnThumbDown.setEnabled(!isRated);

        holder.btnThumbUp.setOnClickListener(v -> {
            holder.btnThumbUp.setEnabled(false);
            holder.btnThumbDown.setEnabled(false);
            sendRating(order, true, position);
        });
        holder.btnThumbDown.setOnClickListener(v -> {
            holder.btnThumbUp.setEnabled(false);
            holder.btnThumbDown.setEnabled(false);
            sendRating(order, false, position);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void sendRating(Order order, boolean isThumbUp, int position) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("order_id", order.getOrderId())
                .add("rating", isThumbUp ? "1" : "0")
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2801261/submit_rating.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Rating", "Failed: " + e.getMessage());
                // Optionally, re-enable buttons on failure
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        notifyItemChanged(position);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    order.setRated(true);
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> notifyItemChanged(position));
                    }
                } else {
                    Log.e("Rating", "Error: " + response.code());
                    // Optionally, re-enable buttons on error
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> notifyItemChanged(position));
                    }
                }
                response.close();
            }
        });
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvRestaurant, tvStatus;
        ImageButton btnThumbUp, btnThumbDown;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnThumbUp = itemView.findViewById(R.id.btnThumbUp);
            btnThumbDown = itemView.findViewById(R.id.btnThumbDown);
        }
    }
}

