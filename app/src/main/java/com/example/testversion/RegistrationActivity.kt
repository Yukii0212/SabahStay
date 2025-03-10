package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
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
        val genderInput = findViewById<EditText>(R.id.genderInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordInput)
        val passwordRequirementsText = findViewById<TextView>(R.id.passwordRequirementsText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginText = findViewById<TextView>(R.id.loginText)

        // Password validation feedback
        val passwordTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updatePasswordRequirements(
                    passwordInput.text.toString(),
                    confirmPasswordInput.text.toString(),
                    passwordRequirementsText
                )
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        passwordInput.addTextChangedListener(passwordTextWatcher)
        confirmPasswordInput.addTextChangedListener(passwordTextWatcher)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val passport = passportInput.text.toString().trim()
            val gender = genderInput.text.toString().trim().lowercase()
            val phone = phoneInput.text.toString().trim()
            val email = emailInput.text.toString().trim().lowercase()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            // Validate inputs
            if (name.isEmpty() || passport.isEmpty() || gender.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidGender(gender)) {
                Toast.makeText(this, "Invalid gender. Enter Male, Female, or Other", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isPasswordValid(password)) {
                Toast.makeText(this, "Password does not meet requirements", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val userDao = UserDatabase.getDatabase(applicationContext).userDao()
                val existingUser = userDao.getUserByEmailOrPhoneOrPassport(email, phone, passport)

                if (existingUser == null) {
                    userDao.insert(User(name, passport, gender, phone, email, password))
                    runOnUiThread {
                        Toast.makeText(this@RegistrationActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@RegistrationActivity, "Email, phone, or passport already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // Validate email format
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validate password rules
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() }
    }

    // Validate gender input
    private fun isValidGender(gender: String): Boolean {
        return gender == "male" || gender == "female" || gender == "other"
    }

    // Updates Password Requirements dynamically ✅/❌
    private fun updatePasswordRequirements(password: String, confirmPassword: String, textView: TextView) {
        val requirements = listOf(
            Pair("6 Characters long", password.length >= 6),
            Pair("1 Uppercase letter", password.any { it.isUpperCase() }),
            Pair("1 Lowercase letter", password.any { it.isLowerCase() }),
            Pair("1 Number", password.any { it.isDigit() }),
            Pair("Passwords match", password == confirmPassword)
        )

        textView.text = requirements.joinToString("\n") { (req, met) ->
            if (met) "✅ $req" else "❌ $req"
        }
    }
}
