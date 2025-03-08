package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.User
import com.example.testversion.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val passportInput = findViewById<EditText>(R.id.passportInput)
        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginText = findViewById<TextView>(R.id.loginText)

        // Setup gender dropdown with custom styles
        val genders = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, genders)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        genderSpinner.adapter = adapter

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val passport = passportInput.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()
            val phone = phoneInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            // Validation
            if (name.isEmpty() || passport.isEmpty() || phone.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save user to database
            val user = User(name, passport, gender, phone, email, password)

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val userDao = UserDatabase.getDatabase(applicationContext).userDao()
                    val existingUser = userDao.getUserByEmail(email)

                    Log.d("DEBUG", "Checking for existing user with email: $email")

                    if (existingUser == null) {
                        Log.d("DEBUG", "User not found, inserting new user.")
                        userDao.insert(user)

                        runOnUiThread {
                            Toast.makeText(this@RegistrationActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.d("DEBUG", "User already exists!")
                        runOnUiThread {
                            Toast.makeText(this@RegistrationActivity, "Email already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", "Exception during registration: ${e.localizedMessage}", e)
                    runOnUiThread {
                        Toast.makeText(this@RegistrationActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Redirect to Login when "Login now" is clicked
        loginText.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}
