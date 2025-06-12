package com.kapasiya.sharefair;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private CredentialManager credentialManager;
    private TextInputEditText phoneEditText;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // This callback is invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:" + credential);
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.w(TAG, "onVerificationFailed", e);
            Toast.makeText(LoginActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            LoginActivity.this.verificationId = verificationId;
            LoginActivity.this.resendToken = token;

            Toast.makeText(LoginActivity.this, "OTP sent to your phone", Toast.LENGTH_SHORT).show();

            // Navigate to OTP verification activity
            Intent intent = new Intent(LoginActivity.this, OTPVerificationActivity.class);
            intent.putExtra("verificationId", verificationId);
            intent.putExtra("phoneNumber", Objects.requireNonNull(phoneEditText.getText()).toString().trim());
            startActivity(intent);
        }
    };

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
        MaterialButton googleLoginButton = findViewById(R.id.googleLoginButton);
        MaterialButton sendOtpButton = findViewById(R.id.sendOtpButton);
        phoneEditText = findViewById(R.id.phoneEditText);

        // Set up Google Login button with Credential Manager
        googleLoginButton.setOnClickListener(view -> signInWithGoogle());

        // Set up Send OTP button
        sendOtpButton.setOnClickListener(view -> {
            String phoneNumber = Objects.requireNonNull(phoneEditText.getText()).toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add country code if not present
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+91" + phoneNumber; // Assuming Indian numbers, change as needed
            }

            sendVerificationCode(phoneNumber);
        });

        // Set up Sign Up text click listener
        findViewById(R.id.signUpText).setOnClickListener(view -> {
            // Navigate to sign up activity or handle sign up logic
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            Toast.makeText(LoginActivity.this, "Sign up functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void signInWithGoogle() {
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
                            Log.w(TAG, "Google Sign-In failed", e);
                            Toast.makeText(LoginActivity.this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

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
                            Glide.with(LoginActivity.this).load(auth.getCurrentUser().getPhotoUrl());
                        }

                        Log.w(TAG, "Name: " + (auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : "null"));
                        Log.w(TAG, "Mail: " + (auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "null"));

                        Toast.makeText(LoginActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(LoginActivity.this, "Phone authentication successful!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Phone authentication failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}