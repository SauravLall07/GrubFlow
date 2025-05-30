package com.example.mainapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

        TextView message = findViewById(R.id.member_names);
        TextView message2 = findViewById(R.id.phone_text);
        TextView message3 = findViewById(R.id.member);
        TextView message4 = findViewById(R.id.meber_nam3);

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), "Mesh!, Bring lozenges", Toast.LENGTH_SHORT).show();
            }
        });
        message2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), "Nein", Toast.LENGTH_SHORT).show();
            }
        });
        message3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), "Plowing mech", Toast.LENGTH_SHORT).show();
            }
        });
        message4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), "Aight", Toast.LENGTH_SHORT).show();
            }
        });


        setupContactCardListeners();
    }

    private void setupContactCardListeners() {

        emailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        phoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    public void updateMissionText(String newMission) {
        missionText.setText(newMission);
    }


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
