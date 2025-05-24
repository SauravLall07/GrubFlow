package com.example.mainapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateNewOrderActivity extends AppCompatActivity {

    private EditText etRestaurantName;
    private EditText etItemName;
    private NumberPicker numberPickerQty;
    private Button btnSubmitOrder;

    private final OkHttpClient client = new OkHttpClient();
    private static final String CREATE_ORDER_URL =
            "https://lamp.ms.wits.ac.za/home/s2801261/create_order.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_new_order);

        // Initialize views
        etRestaurantName = findViewById(R.id.etRestaurantName);
        etItemName = findViewById(R.id.etItemName);
        numberPickerQty = findViewById(R.id.numberPickerQty);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);

        // Configure number picker
        numberPickerQty.setMinValue(1);
        numberPickerQty.setMaxValue(100);

        // Get logged-in user ID
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        final int userId = prefs.getInt("user_id", -1);

        if(userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnSubmitOrder.setOnClickListener(v -> validateAndSubmitOrder(userId));
    }

    private void validateAndSubmitOrder(int userId) {
        String restaurantName = etRestaurantName.getText().toString().trim();
        String itemName = etItemName.getText().toString().trim();
        int quantity = numberPickerQty.getValue();

        // Validate inputs
        if(restaurantName.isEmpty()) {
            etRestaurantName.setError("Restaurant name required");
            return;
        }

        if(itemName.isEmpty()) {
            etItemName.setError("Item name required");
            return;
        }

        // Hardcoded order defaults
        String status = "pending";
        boolean isPaid = false;

        // Build request body
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .add("restaurant_name", restaurantName)
                .add("item_name", itemName)
                .add("quantity", String.valueOf(quantity))
                .add("status", status)
                .add("isPaid", isPaid ? "1" : "0")
                .build();

        submitOrderToServer(formBody);
    }

    private void submitOrderToServer(RequestBody formBody) {
        Request request = new Request.Builder()
                .url(CREATE_ORDER_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(CreateNewOrderActivity.this,
                                "Network error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                runOnUiThread(() -> handleServerResponse(responseBody));
            }
        });
    }

    private void handleServerResponse(String response) {
        if(response.contains("success")) {
            // Clear fields on success
            etRestaurantName.setText("");
            etItemName.setText("");
            numberPickerQty.setValue(1);

            Toast.makeText(this,
                    "Order created successfully!",
                    Toast.LENGTH_LONG).show();
        }
        else if(response.contains("Restaurant not found")) {
            etRestaurantName.setError("Restaurant not found in system");
        }
        else {
            Toast.makeText(this,
                    "Order failed: " + response,
                    Toast.LENGTH_LONG).show();
        }
    }
}