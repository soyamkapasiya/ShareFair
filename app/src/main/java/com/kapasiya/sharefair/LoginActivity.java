package com.kapasiya.sharefair;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private CredentialManager credentialManager;
    private TextInputEditText emailLoginText;
    private TextInputEditText passwordLoginText;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private MaterialButton loginButton;
    private MaterialButton googleLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check if user is already signed in
        if (auth.getCurrentUser() != null) {
            navigateToMainActivity();
            return;
        }

        // Initialize Credential Manager (modern replacement for Google Sign-In)
        credentialManager = CredentialManager.create(this);

        // Initialize views
        googleLoginButton = findViewById(R.id.googleLoginButton);
        loginButton = findViewById(R.id.loginButton);
        emailLoginText = findViewById(R.id.emailLogin);
        passwordLoginText = findViewById(R.id.passwordLogin);

        // Get TextInputLayout references for error handling
        emailInputLayout = (TextInputLayout) emailLoginText.getParent().getParent();
        passwordInputLayout = (TextInputLayout) passwordLoginText.getParent().getParent();

        googleLoginButton.setOnClickListener(view -> signInWithGoogle());

        // Set up Login button
        loginButton.setOnClickListener(view -> loginWithEmailAndPassword());

        // Set up Sign Up text click listener
        findViewById(R.id.signUpText).setOnClickListener(view -> {
            // Navigate to sign up activity
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void loginWithEmailAndPassword() {
        String email = emailLoginText.getText().toString().trim();
        String password = passwordLoginText.getText().toString().trim();

        // Clear previous errors
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);

        // Validate input
        if (!validateInput(email, password)) {
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Sign in with Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoadingState(false);

                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null) {
                            // Check if email is verified
                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                navigateToMainActivity();
                            } else {
                                // Email not verified, show verification dialog
                                showEmailVerificationDialog(user);
                            }
                        }
                    } else {
                        // Sign in failed
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        handleLoginError(task.getException());
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    private void handleLoginError(Exception exception) {
        String errorMessage = "Login failed. Please try again.";

        if (exception instanceof FirebaseAuthInvalidUserException) {
            String errorCode = ((FirebaseAuthInvalidUserException) exception).getErrorCode();
            if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                emailInputLayout.setError("No account found with this email");
                errorMessage = "Account not found. Please check your email or sign up.";
            } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                errorMessage = "This account has been disabled. Please contact support.";
            }
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            String errorCode = ((FirebaseAuthInvalidCredentialsException) exception).getErrorCode();
            if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                emailInputLayout.setError("Invalid email format");
            } else if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                passwordInputLayout.setError("Incorrect password");
                errorMessage = "Incorrect password. Please try again.";
            }
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void showEmailVerificationDialog(FirebaseUser user) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Email Verification Required")
                .setMessage("Please verify your email address to continue. Check your email for a verification link.")
                .setPositiveButton("Resend Email", (dialog, which) -> {
                    user.sendEmailVerification()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,
                                            "Verification email sent!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Sign out the user since email is not verified
                    auth.signOut();
                })
                .setCancelable(false)
                .show();
    }

    private void setLoadingState(boolean isLoading) {
        loginButton.setEnabled(!isLoading);
        googleLoginButton.setEnabled(!isLoading);

        if (isLoading) {
            loginButton.setText("Signing in...");
        } else {
            loginButton.setText("Login");
        }
    }

    private void signInWithGoogle() {
        // Show loading state
        setLoadingState(true);

        // Create GetGoogleIdOption (replacement for GoogleSignInOptions)
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.client_id)) // Your web client ID
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
                            setLoadingState(false);
                            Log.w(TAG, "Google Sign-In failed", e);
                            Toast.makeText(LoginActivity.this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } else {
            setLoadingState(false);
            Toast.makeText(this, "Google Sign-In requires Android API level 28 or higher", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGoogleSignInResult(GetCredentialResponse result) {
        try {
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(result.getCredential().getData());

            String idToken = googleIdTokenCredential.getIdToken();

            // Create Firebase credential
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);

            // Sign in to Firebase
            auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, task -> {
                        setLoadingState(false);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            // Load user profile image
                            if (auth.getCurrentUser() != null && auth.getCurrentUser().getPhotoUrl() != null) {
                                Glide.with(LoginActivity.this).load(auth.getCurrentUser().getPhotoUrl());
                            }

                            Log.d(TAG, "Name: " + (auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : "null"));
                            Log.d(TAG, "Email: " + (auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "null"));

                            Toast.makeText(LoginActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed: " +
                                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            setLoadingState(false);
            Log.e(TAG, "Error handling Google Sign-In result", e);
            Toast.makeText(this, "Error processing Google Sign-In", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}