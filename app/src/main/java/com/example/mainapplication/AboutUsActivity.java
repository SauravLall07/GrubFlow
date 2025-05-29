package com.example.mainapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class AboutUsActivity extends AppCompatActivity {
    private TextView missionText;
    private MaterialCardView emailCard;
    private MaterialCardView phoneCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Initialize views
        missionText = findViewById(R.id.mission_text);
        emailCard = findViewById(R.id.email_card);
        phoneCard = findViewById(R.id.phone_card);

        // Set up click listeners for contact cards
        setupContactCardListeners();
    }

    private void setupContactCardListeners() {
        // Email card click listener
        emailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle email click - you can add email intent here
            }
        });

        // Phone card click listener
        phoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle phone click - you can add phone intent here
            }
        });
    }

    // Method to update mission text dynamically
    public void updateMissionText(String newMission) {
        missionText.setText(newMission);
    }

    // Method to update contact information
    public void updateContactInfo(String email, String phone) {
        TextView emailTextView = findViewById(R.id.email_text);
        TextView phoneTextView = findViewById(R.id.phone_text);

        if (emailTextView != null) {
            emailTextView.setText(email);
        }
        if (phoneTextView != null) {
            phoneTextView.setText(phone);
        }
    }
}
