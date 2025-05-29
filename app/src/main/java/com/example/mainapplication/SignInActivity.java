package com.example.mainapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

        // Request notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        loginButton  = findViewById(R.id.btnLogin);
        emailField   = findViewById(R.id.etEmail);
        passwordField= findViewById(R.id.etPassword);

        loginButton.setOnClickListener(v -> loginUser());

        TextView tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        CheckBox rememberMe = findViewById(R.id.cbRememberMe);
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("rememberMe", false)) {
            emailField.setText(prefs.getString("email", ""));
            passwordField.setText(prefs.getString("password", ""));
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Welcome Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Welcome messages when you log in");
            channel.enableLights(true);
            channel.setLightColor(0xFF00FF00);
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }

        Intent notifIntent = new Intent(this, SignInActivity.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notifIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_transparent)
                .setContentText("Severance reconnected. Welcome back, " + name + ".")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("The culinary matrix awaits your command, " + name + "."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setColor(getColor(R.color.notification_color))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOnlyAlertOnce(true)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent);

        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginUser() {
        String email    = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

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
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2801261/login2.php");
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
                JSONObject json    = jsonArray.getJSONObject(0);

                runOnUiThread(() -> {
                    try {
                        if (json.getBoolean("success")) {
                            String name = json.getString("name");
                            int userId = json.getInt("user_id");
                            String role = json.getString("role");
                            int restaurantId = json.getInt("restaurant_id"); // ← parsed from PHP

                            // Show notification
                            showWelcomeNotification(name);

                            Toast.makeText(this, "Login Successful!! Welcome " + name, Toast.LENGTH_LONG).show();

                            SharedPreferences globalPrefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = globalPrefs.edit();
                            editor.putString("staff_name", name);
                            editor.putInt("user_id", userId);
                            editor.putInt("restaurant_id", restaurantId);
                            editor.apply();

                            Intent intent;
                            if ("staff".equals(role)) {
                                intent = new Intent(SignInActivity.this, StaffMenuActivity.class);
                                intent.putExtra("staff_name",    name);
                                intent.putExtra("restaurant_id", restaurantId);  // ← added
                            } else {
                                editor.putString("customer_name", name);
                                editor.apply();
                                intent = new Intent(SignInActivity.this, CustomerActivity.class);
                                intent.putExtra("customer_name", name);
                            }

                            // Remember-me
                            SharedPreferences loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor loginEditor = loginPrefs.edit();
                            CheckBox rememberMe = findViewById(R.id.cbRememberMe);
                            if (rememberMe.isChecked()) {
                                loginEditor.putBoolean("rememberMe", true);
                                loginEditor.putString("email", email);
                                loginEditor.putString("password", password);
                            } else {
                                loginEditor.clear();
                            }
                            loginEditor.apply();

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
                runOnUiThread(() ->
                        Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}
