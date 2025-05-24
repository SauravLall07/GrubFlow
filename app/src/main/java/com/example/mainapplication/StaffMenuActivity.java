package com.example.mainapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class StaffMenuActivity extends AppCompatActivity {

    private MaterialButton btnNewOrder, btnEditOrder;
    private TextInputEditText etCustomerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);

        // Find views
        btnNewOrder = findViewById(R.id.btnNewOrder);
        btnEditOrder = findViewById(R.id.btnEditOrder);
        etCustomerName = findViewById(R.id.etCustomerName);

        // Handle new order
        btnNewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(StaffMenuActivity.this, CreateNewOrderActivity.class);
            startActivity(intent);
        });

        // Handle view/edit order
        btnEditOrder.setOnClickListener(v -> {
            String customerName = etCustomerName.getText().toString().trim();

            if (customerName.isEmpty()) {
                etCustomerName.setError("Please enter a customer name");
                return;
            }

            // Pass customer name to EditOrderActivity
            Intent intent = new Intent(StaffMenuActivity.this, EditOrderActivity.class);
            intent.putExtra("customer_name", customerName);
            startActivity(intent);
        });
    }
}
