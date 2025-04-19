package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class BranchOverview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch_overview)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val profileButton = findViewById<Button>(R.id.profileButton)
        val paymentButton = findViewById<Button>(R.id.paymentButton)
        val bookingButton = findViewById<Button>(R.id.bookingButton)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            loginButton.visibility = Button.GONE
        } else {
            loginButton.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        profileButton.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        paymentButton.setOnClickListener {
            startActivity(Intent(this, PaymentDetailsActivity::class.java))
        }

        bookingButton.setOnClickListener {
            Log.d("BranchOverview", "Booking button clicked")
            Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SearchAvailableRoomActivity::class.java))
        }
    }
}
