package com.example.mainapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class RegisterActivity extends AppCompatActivity {

    TextView returnToLogin;
    EditText etName, etEmail, etPassword, etConfirmPassword, etRestaurant;
    AutoCompleteTextView etRole;
    Button btnRegister;
    OkHttpClient client;
    LinearLayout layoutRestaurant;
    String url = "https://lamp.ms.wits.ac.za/home/s2801261/register.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvRegister), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        returnToLogin = findViewById(R.id.tvLogin);

        returnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRole = findViewById(R.id.etRole);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etRestaurant = findViewById(R.id.etRestaurant);
        layoutRestaurant = findViewById(R.id.layoutRestaurant);

        String[] roles = {"customer", "staff"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        etRole.setAdapter(adapter);


// Optional: show dropdown when focused
        etRole.setOnClickListener(v -> etRole.showDropDown());
        etRole.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRole = parent.getItemAtPosition(position).toString().toLowerCase();
            layoutRestaurant.setVisibility(selectedRole.equals("staff") ? View.VISIBLE : View.GONE);
        });
        etRestaurant.setOnClickListener(v -> showRestaurantDialog(etRestaurant));

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }


    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = etRole.getText().toString().trim().toLowerCase();

        String restaurant = etRestaurant.getText().toString().trim();

        if (role.equals("staff") && restaurant.isEmpty()) {
            etRestaurant.setError("Please select a restaurant");
            Toast.makeText(this, "Staff must select a restaurant", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasError = false;

        // Name validation
        if (name.isEmpty()) {
            etName.setError("Full name is required");
            hasError = true;
        }

        // Email validation
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            hasError = true;
        } else {
            String[] validDomains = {
                    "@gmail.com", "@yahoo.com", "@wits.ac.za",
                    "@hotmail.com", "@outlook.com", "@icloud.com", "@students.wits.ac.za"
            };
            boolean validEmailSuffix = false;
            for (String domain : validDomains) {
                if (email.endsWith(domain)) {
                    validEmailSuffix = true;
                    break;
                }
            }
            if (!validEmailSuffix) {
                etEmail.setError("Email must end with a valid domain (e.g. @gmail.com)");
                hasError = true;
            }
        }

        // Check if password is empty
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            hasError = true;
        }

        // Return early if name, email, or role OR empty password has errors
        if (hasError) {
            return;
        }

        // Password strength validation (only if not empty)
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        if (!password.matches(passwordPattern)) {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid Password")
                    .setMessage("Password must be at least 8 characters and include:\n- 1 lowercase letter\n- 1 uppercase letter\n- 1 number\n- 1 special character")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }


        if (!password.equals(confirmPassword)) {
            new AlertDialog.Builder(this)
                    .setTitle("Passwords Do Not Match")
                    .setMessage("Please confirm your password again.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("password", password)
                .add("role", role)
                .add("restaurant", restaurant)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responseString = response.body().string();

                runOnUiThread(() -> {
                    try {
                        org.json.JSONArray jsonArray = new org.json.JSONArray(responseString);
                        JSONObject json = jsonArray.getJSONObject(0);

                        boolean success = json.getBoolean("success");
                        String message = json.getString("message");

                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                        if (success) {
                            startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
                            finish();
                        }

                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Response parsing error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showRestaurantDialog(EditText targetEditText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Restaurant");

        // Dummy data — replace with real restaurant list from API
        String[] restaurants = {"Burger House", "Sushi Corner", "Pizza Palace", "McDonald's", "KFC", "Nando's"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurants);

        // Add search bar
        final EditText input = new EditText(this);
        input.setHint("Search...");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setPadding(50, 30, 50, 30);

        ListView listView = new ListView(this);
        listView.setAdapter(adapter);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(listView);

        builder.setView(layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = adapter.getItem(position);
            targetEditText.setText(selected);
            dialog.dismiss();
        });
    }


}