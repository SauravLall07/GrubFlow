package com.example.mainapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateNewOrderActivity extends AppCompatActivity {

    private EditText etStaffName, etTime, etCustomerName, etRestaurantName, etItemName;
    private NumberPicker numberPickerQty;
    private Button btnSubmitOrder;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_new_order);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etStaffName = findViewById(R.id.etStaffName);
        etTime = findViewById(R.id.etTime);
        etCustomerName = findViewById(R.id.etCustomerName);
        etRestaurantName = findViewById(R.id.etRestaurantName);
        etItemName = findViewById(R.id.etItemName);
        numberPickerQty = findViewById(R.id.numberPickerQty);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);

        client = new OkHttpClient();

        loadStaffName();
        setCurrentDateTime();

        numberPickerQty.setMinValue(1);
        numberPickerQty.setMaxValue(20);
        numberPickerQty.setValue(1);

        btnSubmitOrder.setOnClickListener(v -> submitOrder());
    }

    private void loadStaffName() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String staffName = prefs.getString("staff_name", null);
        if (staffName != null) {
            etStaffName.setText(staffName);
            etStaffName.setEnabled(false);
        }
    }

    private void setCurrentDateTime() {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        etTime.setText(currentTime);
        etTime.setEnabled(false);
    }

    private void submitOrder() {
        String staffName = etStaffName.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String customerName = etCustomerName.getText().toString().trim();
        String restaurantName = etRestaurantName.getText().toString().trim();
        String itemName = etItemName.getText().toString().trim();
        int quantity = numberPickerQty.getValue();

        if (customerName.isEmpty()) {
            etCustomerName.setError("Customer name is required");
            etCustomerName.requestFocus();
            return;
        }
        if (restaurantName.isEmpty()) {
            etRestaurantName.setError("Restaurant name is required");
            etRestaurantName.requestFocus();
            return;
        }
        if (itemName.isEmpty()) {
            etItemName.setError("Item name is required");
            etItemName.requestFocus();
            return;
        }

        btnSubmitOrder.setEnabled(false);

        RequestBody formBody = new FormBody.Builder()
                .add("staff_name", staffName)
                .add("order_time", time)
                .add("customer_name", customerName)
                .add("restaurant_name", restaurantName)
                .add("item_name", itemName)
                .add("quantity", String.valueOf(quantity))
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2801261/create_order.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    btnSubmitOrder.setEnabled(true);
                    Toast.makeText(CreateNewOrderActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body() != null ? response.body().string() : "";
                runOnUiThread(() -> {
                    btnSubmitOrder.setEnabled(true);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(respStr);
                            if (json.getBoolean("success")) {
                                Toast.makeText(CreateNewOrderActivity.this, "Order created successfully!", Toast.LENGTH_LONG).show();
                                clearForm();
                            } else {
                                Toast.makeText(CreateNewOrderActivity.this, "Failed: " + json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(CreateNewOrderActivity.this, "Response parse error", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CreateNewOrderActivity.this, "Server error: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void clearForm() {
        etCustomerName.setText("");
        etRestaurantName.setText("");
        etItemName.setText("");
        numberPickerQty.setValue(1);
        setCurrentDateTime();
    }
}
