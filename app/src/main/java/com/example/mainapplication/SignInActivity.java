package com.example.mainapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.CheckBox;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class SignInActivity extends AppCompatActivity {

    Button loginButton;
    EditText emailField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvRegister), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton = findViewById(R.id.btnLogin);
        emailField = findViewById(R.id.etEmail);
        passwordField = findViewById(R.id.etPassword);

        loginButton.setOnClickListener(v -> loginUser());

        TextView tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        CheckBox rememberMe = findViewById(R.id.cbRememberMe);
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (preferences.getBoolean("rememberMe", false)) {
            emailField.setText(preferences.getString("email", ""));
            passwordField.setText(preferences.getString("password", ""));
            rememberMe.setChecked(true);
        }
    }

    public void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Field validation
        boolean hasError = false;
        if (email.isEmpty()) {
            emailField.setError("Email is required");
            hasError = true;
        }
        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            hasError = true;
        }
        if (hasError) return;

        new Thread(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2801261/login.php");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "email=" + email + "&password=" + password;
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                Scanner input = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (input.hasNext()) response.append(input.nextLine());
                input.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                JSONObject json = jsonArray.getJSONObject(0);

                runOnUiThread(() -> {
                    try {
                        if (json.getBoolean("success")) {
                            Toast.makeText(this, "Login Successful!! Welcome " + json.getString("name"), Toast.LENGTH_LONG).show();

                            // Extract values
                            int userId = json.getInt("user_id");        // ← NEW: get user_id
                            String name = json.getString("name");
                            String role = json.getString("role");

                            // Save into SharedPreferences
                            SharedPreferences globalPrefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor globalEditor = globalPrefs.edit();
                            globalEditor.putString("staff_name", name);
                            globalEditor.putInt("user_id", userId);    // ← NEW: store user_id
                            globalEditor.apply();
                            // … after globalEditor.apply():

                            Intent intent;
                            if (role.equals("staff")) {
                                intent = new Intent(SignInActivity.this, StaffMenuActivity.class);
                                intent.putExtra("staff_name", name);
                            } else {
                                globalEditor.putString("customer_name", name);
                                globalEditor.apply();
                                intent = new Intent(SignInActivity.this, CustomerActivity.class);
                                intent.putExtra("customer_name", name);
                            }SS

                            // Remember-me prefs
                            SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
                            CheckBox rememberMe = findViewById(R.id.cbRememberMe);
                            if (rememberMe.isChecked()) {
                                editor.putBoolean("rememberMe", true);
                                editor.putString("email", email);
                                editor.putString("password", password);
                            } else {
                                editor.clear();
                            }
                            editor.apply();

                            startActivity(intent);
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "JSON Error", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
