package com.example.mainapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.widget.CheckBox;


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
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        CheckBox rememberMe = findViewById(R.id.cbRememberMe);
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

// Autofill if saved
        if (preferences.getBoolean("rememberMe", false)) {
            emailField.setText(preferences.getString("email", ""));
            passwordField.setText(preferences.getString("password", ""));
            rememberMe.setChecked(true);
        }


    }

    public void loginUser(){

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        new Thread(() -> {

            try{
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
                while(input.hasNext()){
                    response.append(input.nextLine());
                }
                input.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                JSONObject json = jsonArray.getJSONObject(0);

                runOnUiThread(() -> {
                    try{
                        if(json.getBoolean("success")){
                            Toast.makeText(this, "Login Successful!! Welcome " + json.getString("name"), Toast.LENGTH_LONG).show();

                            String name = json.getString("name");
                            String role = json.getString("role");
                            Intent intent = null;
                            if(role.equals("staff")){
                                intent = new Intent(SignInActivity.this, StaffActivity.class);
                            }
                            //Note that we will need to change this cline once we have made a customer activity
                            else if(role.equals("customer")){
                                intent = new Intent(SignInActivity.this, CustomerActivity.class);
                            }
                            SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
                            if (((CheckBox)findViewById(R.id.cbRememberMe)).isChecked()) {
                                editor.putBoolean("rememberMe", true);
                                editor.putString("email", email);
                                editor.putString("password", password);
                            } else {
                                editor.clear();
                            }
                            editor.apply();

                            startActivity(intent);
                        }else{
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, "JSON Error", Toast.LENGTH_LONG).show();
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show());
            }

        }).start();
    }
}