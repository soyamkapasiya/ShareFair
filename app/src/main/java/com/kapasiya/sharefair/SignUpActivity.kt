package com.kapasiya.sharefair

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.kapasiya.sharefair.ui.theme.ShareFairTheme
import com.kapasiya.sharefair.ui.screens.SignUpScreen
import com.kapasiya.sharefair.model.Group
import com.kapasiya.sharefair.model.User
import com.kapasiya.sharefair.data.RepositoryModule
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class SignUpActivity : ComponentActivity() {

    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        setContent {
            ShareFairTheme {
                SignUpScreen(
                    onSignUp = { name, email, password -> 
                        createAccountWithEmail(name, email, password)
                    },
                    onGoogleSignIn = { signInWithGoogle() },
                    onSignIn = {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    private fun createAccountWithEmail(fullNameText: String, emailText: String, passwordText: String) {
        auth.createUserWithEmailAndPassword(emailText, passwordText)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullNameText)
                            .build()
                        it.updateProfile(profileUpdates)
                        
                        // Create user profile in DB immediately
                        syncUserWithDatabase(it, fullNameText)
                        sendEmailVerification(it, emailText)
                    }
                } else {
                    handleSignUpError(task.exception)
                }
            }
    }

    private fun sendEmailVerification(user: FirebaseUser, emailText: String) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, EmailVerificationActivity::class.java)
                intent.putExtra("email", emailText)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun handleSignUpError(exception: Exception?) {
        val message = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Password is too weak"
            is FirebaseAuthUserCollisionException -> "Account already exists"
            else -> "Sign up failed: ${exception?.message}"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        val executor = ContextCompat.getMainExecutor(this)
        credentialManager.getCredentialAsync(this, request, null, executor,
            object : androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
                override fun onResult(result: GetCredentialResponse) {
                    try {
                        val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
                        auth.signInWithCredential(GoogleAuthProvider.getCredential(credential.idToken, null))
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
                                    Toast.makeText(this@SignUpActivity, "Google Sign-in failed", Toast.LENGTH_LONG).show()
                                }
                            }
                    } catch (e: Exception) {
                        Log.e(TAG, "Google ID Token parsing failed", e)
                    }
                }
                override fun onError(e: GetCredentialException) {
                    Log.e(TAG, "Auth failed: ${e.javaClass.simpleName}", e)
                }
            }
        )
    }

    private fun syncUserWithDatabase(firebaseUser: FirebaseUser, manualName: String? = null) {
        val userRepo = RepositoryModule.userRepository
        val groupRepo = RepositoryModule.groupRepository
        
        lifecycleScope.launch {
            try {
                val existingProfile = userRepo.getUserProfile(firebaseUser.uid)
                if (existingProfile == null) {
                    val newProfile = User(
                        id = firebaseUser.uid,
                        name = manualName ?: firebaseUser.displayName ?: "User",
                        email = firebaseUser.email ?: "",
                        profileImageUrl = firebaseUser.photoUrl?.toString() ?: "",
                        totalBalance = 0.0,
                        friends = emptyList(),
                        isPremium = false,
                        groupLimit = 5
                    )
                    userRepo.saveUserProfile(newProfile)
                    
                    // Feature: Auto-create a "Personal" group for individual spending
                    val personalGroup = Group(
                        name = "My Expenses",
                        members = listOf(firebaseUser.uid),
                        type = "PERSONAL",
                        recentActivity = listOf("Welcome to ShareFair! Track your personal spends here.")
                    )
                    groupRepo.createGroup(personalGroup)
                }
                navigateToMainActivity()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync user", e)
                navigateToMainActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
