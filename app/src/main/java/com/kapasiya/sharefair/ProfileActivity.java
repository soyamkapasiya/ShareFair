package com.kapasiya.sharefair;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private ImageView btnEditProfile;
    private TextView tvUserName;
    private TextView tvPhoneNumber;
    private TextView tvEmail;
    private TextView tvCurrencySymbol;

    // Menu options
    private LinearLayout layoutDefaultCurrency;
    private LinearLayout layoutContactUs;
    private LinearLayout layoutInviteFriends;
    private LinearLayout layoutRateUs;
    private LinearLayout layoutFaqs;
    private LinearLayout layoutSettings;
    private LinearLayout layoutLogout;

    // Firebase
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();

        // Initialize views
        initViews();

        // Set up click listeners
        setupClickListeners();

        // Load user data
        loadUserData();
    }

    private void initViews() {
        // Header components
        btnBack = findViewById(R.id.btn_back);
        btnEditProfile = findViewById(R.id.btn_edit_profile);

        // Profile info
        tvUserName = findViewById(R.id.tv_user_name);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvEmail = findViewById(R.id.tv_email);
        tvCurrencySymbol = findViewById(R.id.tv_currency_symbol);

        // Menu options
        layoutDefaultCurrency = findViewById(R.id.layout_default_currency);
        layoutContactUs = findViewById(R.id.layout_contact_us);
        layoutInviteFriends = findViewById(R.id.layout_invite_friends);
        layoutRateUs = findViewById(R.id.layout_rate_us);
        layoutFaqs = findViewById(R.id.layout_faqs);
        layoutSettings = findViewById(R.id.layout_settings);
        layoutLogout = findViewById(R.id.layout_logout);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Edit profile button
        btnEditProfile.setOnClickListener(v -> {
            // Navigate to edit profile activity
            Toast.makeText(ProfileActivity.this, "Edit profile coming soon", Toast.LENGTH_SHORT).show();
        });

        // Default currency
        layoutDefaultCurrency.setOnClickListener(v -> {
            // Navigate to currency selection
            Toast.makeText(ProfileActivity.this, "Currency selection coming soon", Toast.LENGTH_SHORT).show();
        });

        // Contact us
        layoutContactUs.setOnClickListener(v -> {
            // Navigate to contact us
            Toast.makeText(ProfileActivity.this, "Contact us coming soon", Toast.LENGTH_SHORT).show();
        });

        // Invite friends
        layoutInviteFriends.setOnClickListener(v -> {
            // Navigate to invite friends
            Toast.makeText(ProfileActivity.this, "Invite friends coming soon", Toast.LENGTH_SHORT).show();
        });

        // Rate us
        layoutRateUs.setOnClickListener(v -> {
            // Navigate to play store for rating
            Toast.makeText(ProfileActivity.this, "Rate us coming soon", Toast.LENGTH_SHORT).show();
        });

        // FAQs
        layoutFaqs.setOnClickListener(v -> {
            // Navigate to FAQs
            Toast.makeText(ProfileActivity.this, "FAQs coming soon", Toast.LENGTH_SHORT).show();
        });

        // Settings
        layoutSettings.setOnClickListener(v -> {
            // Navigate to settings
            Toast.makeText(ProfileActivity.this, "Settings coming soon", Toast.LENGTH_SHORT).show();
        });

        // Logout button
        layoutLogout.setOnClickListener(v -> signOut());
    }

    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Set user name
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                tvUserName.setText(displayName);
            } else {
                tvUserName.setText("User Name");
            }

            // Set email
            String email = currentUser.getEmail();
            if (email != null && !email.isEmpty()) {
                tvEmail.setText(email);
            } else {
                tvEmail.setText("No email provided");
            }

            // Set phone number
            String phoneNumber = currentUser.getPhoneNumber();
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                tvPhoneNumber.setText(phoneNumber);
            } else {
                tvPhoneNumber.setText("No phone number");
            }
        }
    }

    private void signOut() {
        // Show confirmation or directly sign out
        auth.signOut();

        // Navigate to login activity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(ProfileActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is still authenticated
        if (auth.getCurrentUser() == null) {
            // User is not authenticated, redirect to login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}