package com.example.mainapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffOrderAdapter extends RecyclerView.Adapter<StaffOrderAdapter.ViewHolder> {

    private final Context context;
    private List<Order> orders = new ArrayList<>();
    private final List<String> statusOptions = List.of("Pending", "Ready", "Collected");

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
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvRestaurant, tvDetails, tvPaid;
        Spinner spinnerStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvPaid = itemView.findViewById(R.id.tvPaid);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
        }

        public void bind(Order order) {
            tvOrderId.setText("Order ID: " + order.getOrderId());
            tvRestaurant.setText("Restaurant: " + order.getRestaurantName());
            tvDetails.setText(order.getDetails());

            StatusSpinnerAdapter adapter = new StatusSpinnerAdapter(context, android.R.layout.simple_spinner_item, statusOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(adapter);

            int selectedPosition = statusOptions.indexOf(order.getStatus());
            if (selectedPosition >= 0) {
                spinnerStatus.setSelection(selectedPosition);
            }

            updatePaidText(order.isPaid());

            spinnerStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    String newStatus = parent.getItemAtPosition(position).toString();
                    order.setStatus(newStatus);

                    boolean shouldBePaid = newStatus.equals("Delivered") || newStatus.equals("Collected");
                    order.setPaid(shouldBePaid);
                    updatePaidText(shouldBePaid);

                    updateOrderStatusOnServer(order);
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                }
            });
        }

        private void updatePaidText(boolean isPaid) {
            tvPaid.setText("Paid: " + (isPaid ? "Yes" : "No"));
        }

        private void updateOrderStatusOnServer(Order order) {
            String url = "https://lamp.ms.wits.ac.za/home/s2801261/updatestatus.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> Log.d("UpdateOrder", "Response: " + response),
                    error -> Log.e("UpdateOrder", "Error: " + error.toString())) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("order_id", order.getOrderId());
                    params.put("status", order.getStatus());
                    params.put("is_paid", order.isPaid() ? "1" : "0");
                    return params;
                }
            };

            Volley.newRequestQueue(context).add(request);
        }
    }
}
