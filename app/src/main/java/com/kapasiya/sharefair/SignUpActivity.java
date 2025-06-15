package com.kapasiya.sharefair;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.bumptech.glide.Glide;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText fullName;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputEditText cPassword;
    private MaterialButton createAccount;
    private MaterialButton googleLoginButton;
    private CheckBox termsCheckBox;

    private CredentialManager credentialManager;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Credential Manager (modern replacement for Google Sign-In)
        credentialManager = CredentialManager.create(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        googleLoginButton = findViewById(R.id.googleSignUpButton);
        createAccount = findViewById(R.id.createAccountButton);
        fullName = findViewById(R.id.fullNameSignUp);
        email = findViewById(R.id.emailSignUp);
        password = findViewById(R.id.passwordSignup);
        cPassword = findViewById(R.id.cPasswordSignup);
        termsCheckBox = findViewById(R.id.termsCheckBox);
    }

    private void setupClickListeners() {
        googleLoginButton.setOnClickListener(view -> signInWithGoogle());

        createAccount.setOnClickListener(view -> createAccountWithEmail());

        findViewById(R.id.signInText).setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.backButton).setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.termsText).setOnClickListener(view -> {
            // Open terms and conditions
            Toast.makeText(this, "Terms & Conditions clicked", Toast.LENGTH_SHORT).show();
            // You can open a WebView or another activity with terms
        });
    }

    private void createAccountWithEmail() {
        String fullNameText = fullName.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String cPasswordText = cPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(fullNameText, emailText, passwordText, cPasswordText)) {
            return;
        }

        // Check terms and conditions
        if (!termsCheckBox.isChecked()) {
            Toast.makeText(this, "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent multiple clicks
        createAccount.setEnabled(false);
        createAccount.setText("Creating Account...");

        // Create user with email and password
        auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    createAccount.setEnabled(true);
                    createAccount.setText("Create Account");

                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null) {
                            // Update user profile with full name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullNameText)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                        }
                                    });

                            // Send email verification
                            sendEmailVerification(user, emailText);
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        handleSignUpError(task.getException());
                    }
                });
    }

    private boolean validateInputs(String fullName, String email, String password, String cPassword) {
        if (fullName.isEmpty()) {
            this.fullName.setError("Full name is required");
            this.fullName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            this.email.setError("Email is required");
            this.email.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Please enter a valid email");
            this.email.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            this.password.setError("Password is required");
            this.password.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            this.password.setError("Password must be at least 6 characters");
            this.password.requestFocus();
            return false;
        }

        if (!password.equals(cPassword)) {
            this.cPassword.setError("Passwords do not match");
            this.cPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void sendEmailVerification(FirebaseUser user, String emailText) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this,
                                "Account created! Please check your email for verification.",
                                Toast.LENGTH_LONG).show();

                        // Navigate to OTP verification activity
                        Intent intent = new Intent(SignUpActivity.this, EmailVerificationActivity.class);
                        intent.putExtra("email", emailText);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this,
                                "Failed to send verification email: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to send verification email", task.getException());
                    }
                });
    }

    private void handleSignUpError(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            password.setError("Password is too weak");
            password.requestFocus();
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            email.setError("An account with this email already exists");
            email.requestFocus();
        } else {
            Toast.makeText(this, "Sign up failed: " +
                            (exception != null ? exception.getMessage() : "Unknown error"),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithGoogle() {
        // Create GetGoogleIdOption (replacement for GoogleSignInOptions)
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.client_id))
                .setAutoSelectEnabled(true)
                .build();

        // Create GetCredentialRequest
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        // Get credential using CredentialManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            credentialManager.getCredentialAsync(
                    this,
                    request,
                    null, // CancellationSignal - can be null
                    this.getMainExecutor(), // Executor
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                        @Override
                        public void onResult(GetCredentialResponse result) {
                            handleGoogleSignInResult(result);
                        }

                        @Override
                        public void onError(GetCredentialException e) {
                            Log.w(TAG, "Google Sign-In failed", e);
                            Toast.makeText(SignUpActivity.this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    @SuppressLint("CheckResult")
    private void handleGoogleSignInResult(GetCredentialResponse result) {
        GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(result.getCredential().getData());

        String idToken = googleIdTokenCredential.getIdToken();

        // Create Firebase credential
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);

        // Sign in to Firebase
        auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");

                        // Load user profile image
                        if (auth.getCurrentUser() != null && auth.getCurrentUser().getPhotoUrl() != null) {
                            Glide.with(SignUpActivity.this).load(auth.getCurrentUser().getPhotoUrl());
                        }

                        Log.w(TAG, "Name: " + (auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : "null"));
                        Log.w(TAG, "Mail: " + (auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "null"));

                        Toast.makeText(SignUpActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}