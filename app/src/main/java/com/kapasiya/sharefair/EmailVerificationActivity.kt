package com.kapasiya.sharefair

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.kapasiya.sharefair.ui.theme.ShareFairTheme
import com.kapasiya.sharefair.ui.screens.EmailVerificationScreen

class EmailVerificationActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val userEmail = intent.getStringExtra("email") ?: auth.currentUser?.email ?: ""

        setContent {
            ShareFairTheme {
                EmailVerificationScreen(
                    email = userEmail,
                    onResend = { resendVerificationEmail(userEmail) },
                    onOpenEmail = { openEmailApp() },
                    onDone = { checkEmailVerificationStatus() }
                )
            }
        }
    }

    private fun resendVerificationEmail(email: String) {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Verification email sent to $email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openEmailApp() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
        try {
            startActivity(Intent.createChooser(intent, "Open Email App"))
        } catch (e: Exception) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkEmailVerificationStatus() {
        auth.currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful && auth.currentUser?.isEmailVerified == true) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Email not yet verified", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
