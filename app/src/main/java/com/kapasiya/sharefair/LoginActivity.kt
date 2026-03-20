package com.kapasiya.sharefair

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.kapasiya.sharefair.ui.theme.ShareFairTheme
import com.kapasiya.sharefair.ui.screens.LoginScreen
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        if (auth.currentUser != null) {
            navigateToMainActivity()
            return
        }

        setContent {
            ShareFairTheme {
                LoginScreen(
                    onLogin = { email, password -> loginWithEmailAndPassword(email, password) },
                    onGoogleSignIn = { signInWithGoogle() },
                    onSignUp = {
                        startActivity(Intent(this, SignUpActivity::class.java))
                    }
                )
            }
        }
    }

    private fun loginWithEmailAndPassword(email: String, pword: String) {
        auth.signInWithEmailAndPassword(email, pword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        navigateToMainActivity()
                    } else {
                        Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
                        auth.signOut()
                    }
                } else {
                    handleLoginError(task.exception)
                }
            }
    }

    private fun handleLoginError(exception: Exception?) {
        val message = when (exception) {
            is FirebaseAuthInvalidUserException -> "User not found"
            is FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
            else -> "Login failed: ${exception?.message}"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun signInWithGoogle() {
        val serverClientId = getString(R.string.default_web_client_id)
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setNonce(hashedNonce)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val executor = ContextCompat.getMainExecutor(this)
        credentialManager.getCredentialAsync(
            this, request, null, executor,
            object : androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
                override fun onResult(result: GetCredentialResponse) {
                    try {
                        val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
                        val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val firebaseUser = auth.currentUser
                                    if (firebaseUser != null) {
                                        syncUserWithDatabase(firebaseUser)
                                    } else {
                                        navigateToMainActivity()
                                    }
                                } else {
                                    Log.e(TAG, "Firebase auth failed", task.exception)
                                    Toast.makeText(this@LoginActivity, "Firebase Auth failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } catch (e: Exception) {
                        Log.e(TAG, "Google ID Token parsing failed", e)
                        Toast.makeText(this@LoginActivity, "Parsing error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(e: GetCredentialException) {
                    Log.e(TAG, "Credential Manager failed: ${e.javaClass.simpleName}", e)
                    val friendlyMessage = when(e) {
                        is NoCredentialException -> "No Google accounts found. Please add an account to your device."
                        else -> "Sign-in failed: ${e.message}"
                    }
                    Toast.makeText(this@LoginActivity, friendlyMessage, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun syncUserWithDatabase(firebaseUser: com.google.firebase.auth.FirebaseUser) {
        val userRepo = com.kapasiya.sharefair.data.RepositoryModule.userRepository
        lifecycleScope.launch {
            try {
                val existingProfile = userRepo.getUserProfile(firebaseUser.uid)
                val updatedProfile = com.kapasiya.sharefair.model.User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: existingProfile?.name ?: "User",
                    email = firebaseUser.email ?: existingProfile?.email ?: "",
                    profileImageUrl = firebaseUser.photoUrl?.toString() ?: existingProfile?.profileImageUrl ?: "",
                    totalBalance = existingProfile?.totalBalance ?: 0.0,
                    friends = existingProfile?.friends ?: emptyList()
                )
                userRepo.saveUserProfile(updatedProfile)
                navigateToMainActivity()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync user", e)
                navigateToMainActivity() // Navigate anyway, but log the error
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
