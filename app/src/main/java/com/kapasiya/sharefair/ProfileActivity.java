package com.kapasiya.sharefair;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private ImageView btnEditProfile;

    private ImageView profile;
    private TextView tvUserName;
    private TextView tvPhoneNumber;
    private TextView tvEmail;

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

        // Menu options
        layoutDefaultCurrency = findViewById(R.id.layout_default_currency);
        layoutContactUs = findViewById(R.id.layout_contact_us);
        layoutInviteFriends = findViewById(R.id.layout_invite_friends);
        layoutRateUs = findViewById(R.id.layout_rate_us);
        layoutFaqs = findViewById(R.id.layout_faqs);
        layoutSettings = findViewById(R.id.layout_settings);
        layoutLogout = findViewById(R.id.layout_logout);
        profile = findViewById(R.id.iv_profile_picture);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Edit profile button
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Edit profile coming soon", Toast.LENGTH_SHORT).show();
        });

        // Default currency
        layoutDefaultCurrency.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Currency selection coming soon", Toast.LENGTH_SHORT).show();
        });

        // Contact us
        layoutContactUs.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Contact us coming soon", Toast.LENGTH_SHORT).show();
        });

        // Invite friends
        layoutInviteFriends.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Invite friends coming soon", Toast.LENGTH_SHORT).show();
        });

        // Rate us
        layoutRateUs.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Rate us coming soon", Toast.LENGTH_SHORT).show();
        });

        // FAQs
        layoutFaqs.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "FAQs coming soon", Toast.LENGTH_SHORT).show();
        });

        // Settings
        layoutSettings.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Settings coming soon", Toast.LENGTH_SHORT).show();
        });

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

            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                // Load the image into ImageView
                loadProfileImage(photoUrl.toString());
            } else {
                // No profile picture available, show default image
                profile.setImageResource(R.drawable.circle_background);
            }

        }
    }

    private void loadProfileImage(String photoUrl) {
        ImageView profileImageView = findViewById(R.id.iv_profile_picture);

        Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.circle_background) // Show while loading
                .error(R.drawable.circle_background) // Show if loading fails
                .circleCrop() // Makes image circular
                .into(profileImageView);
    }

    private void signOut() {
        auth.signOut();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(ProfileActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}