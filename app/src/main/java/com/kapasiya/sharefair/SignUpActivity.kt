package com.kapasiya.sharefair

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
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
                    val user = auth.currentUser
                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullNameText)
                            .build()
                        it.updateProfile(profileUpdates)
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
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.client_id))
            .setAutoSelectEnabled(true)
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            credentialManager.getCredentialAsync(this, request, null, mainExecutor,
                object : androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
                    override fun onResult(result: GetCredentialResponse) {
                        val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
                        auth.signInWithCredential(GoogleAuthProvider.getCredential(credential.idToken, null))
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) navigateToMainActivity()
                            }
                    }
                    override fun onError(e: GetCredentialException) { Log.e(TAG, "Auth failed", e) }
                }
            )
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
