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

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orderList = orders;
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

        holder.btnThumbUp.setEnabled(!order.isRated());
        holder.btnThumbDown.setEnabled(!order.isRated());

        View.OnClickListener rateListener = v -> {
            boolean isUp = v.getId() == R.id.btnThumbUp;
            sendRating(order.getOrderId(), isUp);
            order.setRated(true);
            notifyItemChanged(position);
        };

        holder.btnThumbUp.setOnClickListener(rateListener);
        holder.btnThumbDown.setOnClickListener(rateListener);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvRestaurant, tvStatus;
        ImageButton btnThumbUp, btnThumbDown;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurantName);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnThumbUp = itemView.findViewById(R.id.btnThumbUp);
            btnThumbDown = itemView.findViewById(R.id.btnThumbDown);
        }
    }

    private void sendRating(String orderId, boolean isThumbUp) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("order_id", orderId)
                .add("rating", isThumbUp ? "1" : "0")
                .build();

        Request request = new Request.Builder()
                .url("https://yourdomain.com/rate_order.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Rating", "Failed: " + e.getMessage());
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("Rating", "Error: " + response.code());
                }
            }
        });
    }
}
