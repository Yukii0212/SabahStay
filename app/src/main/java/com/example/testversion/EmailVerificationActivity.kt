package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationMessage: TextView
    private lateinit var resendEmailButton: Button
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        auth = FirebaseAuth.getInstance()

        verificationMessage = findViewById(R.id.verificationMessage)
        resendEmailButton = findViewById(R.id.resendEmailButton)
        continueButton = findViewById(R.id.continueButton)

        verificationMessage.text = "A verification email has been sent to ${auth.currentUser?.email}. Please check your inbox."

        resendEmailButton.setOnClickListener {
            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    verificationMessage.text = "Verification email resent. Check your inbox!"
                } else {
                    verificationMessage.text = "Failed to resend email. Try again later."
                }
            }
        }

        continueButton.setOnClickListener {
            auth.currentUser?.reload()?.addOnCompleteListener {
                if (auth.currentUser?.isEmailVerified == true) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    verificationMessage.text = "Email not verified yet. Please check your inbox."
                }
            }
        }
    }
}
