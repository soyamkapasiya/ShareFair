package com.kapasiya.sharefair;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        findViewById(R.id.signInText).setOnClickListener(view -> {
            // Navigate to sign up activity or handle sign up logic
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(SignUpActivity.this, "Sign up functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

}