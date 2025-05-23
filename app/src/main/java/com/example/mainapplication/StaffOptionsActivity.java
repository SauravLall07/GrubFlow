package com.example.mainapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class StaffOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_options);

        Button btnNewOrder = findViewById(R.id.btnNewOrder);
        Button btnEditOrder = findViewById(R.id.btnEditOrder);
        EditText etCustomerName = findViewById(R.id.etCustomerName);

        btnNewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(StaffOptionsActivity.this, StaffActivity.class);
            startActivity(intent);
        });

        btnEditOrder.setOnClickListener(v -> {
            String name = etCustomerName.getText().toString().trim();
            if (!name.isEmpty()) {
                Intent intent = new Intent(StaffOptionsActivity.this, StaffActivity.class);
                intent.putExtra("staff_name", name);
                startActivity(intent);
            } else {
                etCustomerName.setError("Customer name required");
            }
        });
    }
}
