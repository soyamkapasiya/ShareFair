package com.kapasiya.sharefair;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {

    private MaterialButton openEmailButton;
    private MaterialButton skipButton;
    private TextView resendEmailText;
    private TextView timerText;
    private TextView userEmailDisplay;
    private FirebaseAuth auth;
    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Get email from intent or current user
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                userEmail = currentUser.getEmail();
            }
        }

        initializeViews();
        setupClickListeners();
        startResendTimer();

        // Check if user is already verified
        checkEmailVerificationStatus();
    }

    private void initializeViews() {
        openEmailButton = findViewById(R.id.openEmailButton);
        skipButton = findViewById(R.id.skipButton);
        resendEmailText = findViewById(R.id.resendEmailText);
        timerText = findViewById(R.id.timerText);
        userEmailDisplay = findViewById(R.id.userEmailDisplay);

        // Display user's email address
        if (userEmail != null) {
            userEmailDisplay.setText(userEmail);

            // Update the description text to show the email
            TextView emailAddressText = findViewById(R.id.emailAddressText);
            emailAddressText.setText("We sent a verification link to " + userEmail);
        }
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.backButton).setOnClickListener(view -> {
            Intent intent = new Intent(EmailVerificationActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        // Open Email App button
        openEmailButton.setOnClickListener(view -> openEmailApp());

        // Skip button
        skipButton.setOnClickListener(view -> {
            // You can customize this behavior based on your app's requirements
            Toast.makeText(this, "You can verify your email later from settings", Toast.LENGTH_LONG).show();
            navigateToMainActivity();
        });

        // Resend email button
        resendEmailText.setOnClickListener(view -> {
            if (!timerRunning) {
                resendVerificationEmail();
            }
        });
    }

    private void openEmailApp() {
        try {
            // Try to open Gmail first
            Intent gmailIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
            if (gmailIntent != null) {
                startActivity(gmailIntent);
                return;
            }

            // If Gmail is not available, try to open any email app
            Intent emailIntent = new Intent(Intent.ACTION_MAIN);
            emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
                return;
            }

            // If no email app is found, open email chooser
            Intent chooserIntent = new Intent(Intent.ACTION_SEND);
            chooserIntent.setType("message/rfc822");
            chooserIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
            chooserIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            startActivity(Intent.createChooser(chooserIntent, "Open Email App"));

        } catch (Exception e) {
            Toast.makeText(this, "No email app found. Please check your email manually.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error opening email app", e);
        }
    }

    private void checkEmailVerificationStatus() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Reload user to get updated email verification status
            currentUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (currentUser.isEmailVerified()) {
                        Toast.makeText(EmailVerificationActivity.this,
                                "Email already verified!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    }
                } else {
                    Log.e(TAG, "Failed to reload user", task.getException());
                }
            });
        } else {
            Toast.makeText(this, "No user found. Please sign up again.", Toast.LENGTH_SHORT).show();
            navigateToSignUp();
        }
    }

    private void resendVerificationEmail() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EmailVerificationActivity.this,
                                    "Verification email sent to " + userEmail, Toast.LENGTH_SHORT).show();
                            startResendTimer();
                        } else {
                            Toast.makeText(EmailVerificationActivity.this,
                                    "Failed to send verification email: " +
                                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                    Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Failed to send verification email", task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "No user found. Please sign up again.", Toast.LENGTH_SHORT).show();
            navigateToSignUp();
        }
    }

    private void startResendTimer() {
        timerRunning = true;
        resendEmailText.setEnabled(false);
        resendEmailText.setAlpha(0.5f);

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Resend available in " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                resendEmailText.setEnabled(true);
                resendEmailText.setAlpha(1.0f);
                timerText.setText("You can now resend verification email");
            }
        }.start();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(EmailVerificationActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(EmailVerificationActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if email is verified when user returns to the app
        // This is important for when user clicks the verification link and returns
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful() && currentUser.isEmailVerified()) {
                    Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        // Prevent user from going back without verification
        // You can customize this behavior based on your app's requirements
        super.onBackPressed();
    }
}