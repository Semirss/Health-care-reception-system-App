package com.example.hcrs.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.hcrs.R;

public class AdminPageActivity extends AppCompatActivity {

    private CardView cardDoctor, cardReception;
    private ImageView logoutIcon;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        if (!isLoggedIn) {
            // Redirect to AdminLoginActivity if not logged in
            Intent intent = new Intent(this, AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_admin);

        cardDoctor = findViewById(R.id.cardDoctor);
        cardReception = findViewById(R.id.cardReception);
        logoutIcon = findViewById(R.id.logoutIcon);

        cardDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPageActivity.this, AdminDoctorManagerActivity.class);
            startActivity(intent);
        });

        cardReception.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPageActivity.this, AdminReceptionManagerActivity.class);
            startActivity(intent);
        });

        logoutIcon.setOnClickListener(v -> {
            // Clear login state and redirect to AdminLoginActivity
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, false);
            editor.apply();

            Toast.makeText(AdminPageActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AdminPageActivity.this, AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
