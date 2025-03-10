package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Patterns

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
    }

    private fun authenticateUser(identifier: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = UserDatabase.getDatabase(applicationContext).userDao()
            val user = if (Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                userDao.getUserByEmail(identifier.toLowerCase()) // Convert email to lowercase
            } else {
                userDao.getUserByPhone(identifier)
            }

            withContext(Dispatchers.Main) {
                if (user != null && user.password == password) {
                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, FakeMainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
