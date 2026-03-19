package com.kapasiya.sharefair

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.kapasiya.sharefair.ui.theme.ShareFairTheme
import com.kapasiya.sharefair.ui.screens.LoginScreen

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
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.client_id))
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            credentialManager.getCredentialAsync(
                this, request, null, mainExecutor,
                object : androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
                    override fun onResult(result: GetCredentialResponse) {
                        val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
                        auth.signInWithCredential(GoogleAuthProvider.getCredential(credential.idToken, null))
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) navigateToMainActivity()
                            }
                    }

                    override fun onError(e: GetCredentialException) {
                        Log.e(TAG, "Auth failed", e)
                    }
                }
            )
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
