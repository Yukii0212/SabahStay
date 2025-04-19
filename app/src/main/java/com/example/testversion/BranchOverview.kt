package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class BranchOverview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch_overview)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val profileButton = findViewById<Button>(R.id.profileButton)
        val bookingButton = findViewById<Button>(R.id.bookingButton)

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        if (isLoggedIn) {
            loginButton.visibility = View.GONE
        } else {
            loginButton.visibility = View.VISIBLE
            loginButton.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        profileButton.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        bookingButton.setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
        }
    }


}
