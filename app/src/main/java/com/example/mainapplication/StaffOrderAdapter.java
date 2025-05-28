package com.example.mainapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StaffOrderAdapter extends RecyclerView.Adapter<StaffOrderAdapter.ViewHolder> {

    private final Context context;
    private List<Order> orders = new ArrayList<>();

    public StaffOrderAdapter(Context context) {
        this.context = context;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.staff_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffOrderAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderId.setText("Order ID: " + order.getOrderId());
        holder.tvRestaurant.setText("Restaurant: " + order.getRestaurantName());
        holder.tvDetails.setText(order.getDetails());
        holder.btnStatus.setText("Status: " + order.getStatus());
        holder.tvPaid.setText(order.isPaid() ? "Paid: Yes" : "Paid: No");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvRestaurant, tvDetails, tvPaid;
        Button btnStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            btnStatus = itemView.findViewById(R.id.btnStatus);
            tvPaid = itemView.findViewById(R.id.tvPaid);
        }
    }
}
