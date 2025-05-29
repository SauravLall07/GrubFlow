package com.example.mainapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.CheckBox;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;
    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private static final String CHANNEL_ID = "welcome_channel";
    private static final int NOTIFICATION_ID = 1001;

    private Button loginButton;
    private EditText emailField;
    private EditText passwordField;
    private NotificationManagerCompat notificationManager;

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

        // Initialize notification manager
        notificationManager = NotificationManagerCompat.from(this);

        // Initialize notification permission launcher
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Request notification permission if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

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

    private void showWelcomeNotification(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }

        // Create notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Welcome Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Welcome messages when you log in");
            channel.enableLights(true);
            channel.setLightColor(0xFF00FF00); // Green light color
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_transparent)
                .setContentTitle("Welcome Back!")
                .setContentText("Hello " + name + "! We're glad to see you again.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Welcome back to our app, " + name + "! We're excited to have you here again. How can we assist you today?"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setColor(getColor(R.color.notification_color)) // Use your app's color
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOnlyAlertOnce(true)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis());

        // Show notification
        if (notificationManager != null) {
            try {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            } catch (SecurityException e) {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to initialize notification manager", Toast.LENGTH_SHORT).show();
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
                            String name = json.getString("name");

                            // Show welcome notification
                            showWelcomeNotification(name);

                            Toast.makeText(this, "Login Successful!! Welcome " + name, Toast.LENGTH_LONG).show();

                            // Extract values
                            int userId = json.getInt("user_id");
                            String role = json.getString("role");

                            // Save into SharedPreferences
                            SharedPreferences globalPrefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor globalEditor = globalPrefs.edit();
                            globalEditor.putString("staff_name", name);
                            globalEditor.putInt("user_id", userId);
                            globalEditor.apply();

                            Intent intent;
                            if (role.equals("staff")) {
                                intent = new Intent(SignInActivity.this, StaffMenuActivity.class);
                                intent.putExtra("staff_name", name);
                            } else {
                                globalEditor.putString("customer_name", name);
                                globalEditor.apply();
                                intent = new Intent(SignInActivity.this, CustomerActivity.class);
                                intent.putExtra("customer_name", name);
                            }

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
