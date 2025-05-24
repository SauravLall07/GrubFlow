package com.example.mainapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class StaffMenuActivity extends AppCompatActivity {

    private MaterialButton btnNewOrder, btnEditOrder;
    private TextInputEditText etCustomerName;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                // Handle logout
                logout();
                return true;
            }
            return false;
        });

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
    private void logout() {

        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
