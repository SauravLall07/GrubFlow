package com.example.mainapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;




public class StaffOptionsActivity extends AppCompatActivity {

    EditText etCustomerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_options);

        Button btnNewOrder = findViewById(R.id.btnNewOrder);
        Button btnEditOrder = findViewById(R.id.btnEditOrder);
        etCustomerName = findViewById(R.id.etCustomerName);

        btnNewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(StaffOptionsActivity.this, StaffActivity.class);
            startActivity(intent);
        });

        btnEditOrder.setOnClickListener(v -> {
            String name = etCustomerName.getText().toString().trim();
            if (!name.isEmpty()) {
                checkCustomerOrders(name);
            } else {
                etCustomerName.setError("Customer name required");
            }
        });
    }

    private void checkCustomerOrders(String name) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("customer_name", name)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2801261/check_customer_orders.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(StaffOptionsActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String responseString = response.body().string();

                    runOnUiThread(() -> {
                        Log.d("SERVER_RESPONSE", responseString);
                        try {
                            JSONArray jsonArray = new JSONArray(responseString);
                            JSONObject json = jsonArray.getJSONObject(0);

                            if (!json.getBoolean("success")) {
                                etCustomerName.setError(json.getString("message"));
                                return;
                            }

                            Intent intent = new Intent(StaffOptionsActivity.this, OrderHistoryActivity.class);
                            intent.putExtra("customer_name", name);
                            intent.putExtra("orders_json", json.getJSONArray("orders").toString());
                            startActivity(intent);

                        } catch (Exception e) {
                            Toast.makeText(StaffOptionsActivity.this, "Response parsing error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
