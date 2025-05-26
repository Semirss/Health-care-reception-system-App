package com.example.hcrs.Admin;  // Change this to your app package

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminActivity extends AppCompatActivity {

    private CardView cardDoctor, cardReception;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        cardDoctor = findViewById(R.id.cardDoctor);
        cardReception = findViewById(R.id.cardReception);

        cardDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example action for doctor card
                Toast.makeText(AdminActivity.this, "Doctor Card Clicked", Toast.LENGTH_SHORT).show();

                // TODO: Start DoctorActivity or other action
                // Intent intent = new Intent(AdminActivity.this, DoctorActivity.class);
                // startActivity(intent);
            }
        });

        cardReception.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example action for reception card
                Toast.makeText(AdminActivity.this, "Reception Card Clicked", Toast.LENGTH_SHORT).show();

                // TODO: Start ReceptionActivity or other action
                // Intent intent = new Intent(AdminActivity.this, ReceptionActivity.class);
                // startActivity(intent);
            }
        });
    }
}
