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
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userInput = findViewById<EditText>(R.id.userInput)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
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
            val inputType = if (isChecked) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            val typeface = passwordEditText.typeface

            passwordEditText.inputType = inputType
            passwordEditText.typeface = typeface
            passwordEditText.setSelection(passwordEditText.text.length)
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
                    Toast.makeText(
                        this,
                        if (task.isSuccessful) "Reset link sent to your email" else "Failed to send reset email",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
        setupKeyboardScrolling()
    }

    private fun authenticateUser(identifier: String, password: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        loginButton.isEnabled = false
        loginButton.text = "Logging in..."


        firebaseAuth.signInWithEmailAndPassword(identifier, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        loadUserDataFromDatabase(user.email!!)
                    } else {
                        showToast("Please verify your email before logging in.")
                        firebaseAuth.signOut()
                        resetLoginButton()

                    }
                } else {
                    showToast("Invalid credentials or user does not exist")
                    resetLoginButton()
                }
            }
    }

    private fun loadUserDataFromDatabase(email: String) {
        val userDao = UserDatabase.getDatabase(this).userDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByEmail(email)

            if (user != null) {
                val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                editor.putString("full_name", user.name)
                editor.putString("phone", user.phone)
                editor.putString("gender", user.gender)
                editor.putString("email", user.email)
                editor.putBoolean("is_logged_in", true)
                editor.apply()

                withContext(Dispatchers.Main) {
                    showToast("Login Successful")
                    startActivity(Intent(this@LoginActivity, BranchOverview::class.java))
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    showToast("User data not found locally. Please re-register.")
                    resetLoginButton()
                }
            }
        }
    }

    private fun resetLoginButton() {
        loginButton.isEnabled = true
        loginButton.text = "Login"
    }

    private fun showToast(message: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupKeyboardScrolling() {
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollView.postDelayed({
                    scrollView.smoothScrollTo(0, passwordEditText.bottom)
                }, 200)
            }
        }
    }

}

