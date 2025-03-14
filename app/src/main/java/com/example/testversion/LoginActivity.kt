package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userInput = findViewById<EditText>(R.id.userInput)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpText = findViewById<TextView>(R.id.signUpText)

        loginButton.setOnClickListener {
            val userIdentifier = userInput.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (userIdentifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
            } else {
                authenticateUser(userIdentifier, password)
            }
        }

        signUpText.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        val showPasswordCheckBox = findViewById<CheckBox>(R.id.showPasswordCheckBox)

        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordEditText.setSelection(passwordEditText.text.length) // Keep cursor at end
        }

        val forgotPasswordText = findViewById<TextView>(R.id.forgotPasswordText)
        forgotPasswordText.setOnClickListener {
            val email = userInput.text.toString().trim()

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email to reset password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun authenticateUser(identifier: String, password: String) {
        val firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance()

        firebaseAuth.signInWithEmailAndPassword(identifier, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, FakeMainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Please verify your email before logging in.", Toast.LENGTH_LONG).show()
                        firebaseAuth.signOut() // Ensure unverified users can't proceed
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid credentials or user does not exist", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
