package com.example.mainapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private Context context;

    public OrderAdapter(Context context) {
        this.context = context;
        this.orderList = new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        this.orderList = orders != null ? orders : new ArrayList<>();
        Log.d("OrderAdapter", "Orders set: " + this.orderList.size());
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

        holder.tvOrderId.setText("Order #" + order.getOrderId());
        holder.tvRestaurant.setText(order.getRestaurantName());
        holder.tvDetails.setText("Items: " + order.getDetails());
        holder.tvStatus.setText("Status: " + order.getStatus());
        holder.tvPaid.setText("Paid: " + (order.isPaid() ? "Yes" : "No"));

        boolean isRated = order.isRated();
        holder.btnThumbUp.setVisibility(isRated ? View.GONE : View.VISIBLE);
        holder.btnThumbDown.setVisibility(isRated ? View.GONE : View.VISIBLE);

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
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Failed to submit rating", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseStr = response.body().string();
                Log.d("RatingResponse", responseStr);

                boolean success = false;
                try {
                    if (response.isSuccessful()) {
                        JSONObject json = new JSONObject(responseStr);
                        success = json.optBoolean("success", false);
                    } else {
                        Log.e("Rating", "Server Error: " + response.code());
                    }
                } catch (JSONException e) {
                    Log.e("Rating", "JSON error: " + e.getMessage());
                }

                boolean finalSuccess = success;
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (finalSuccess) {
                            order.setRated(true);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Rating submitted!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Rating failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                response.close();
            }
        });
    }



    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvRestaurant, tvDetails, tvStatus, tvPaid;
        ImageButton btnThumbUp, btnThumbDown;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPaid = itemView.findViewById(R.id.tvPaid);
            btnThumbUp = itemView.findViewById(R.id.btnThumbUp);
            btnThumbDown = itemView.findViewById(R.id.btnThumbDown);
        }
    }
}
