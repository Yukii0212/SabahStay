package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
            val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", null)

            if (userEmail.isNullOrEmpty()) {
                Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, SearchAvailableRoomActivity::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)
        }


        // Click listeners for the 3 cards
        val mountainCard = findViewById<CardView>(R.id.mountainCard)
        val islandCard = findViewById<CardView>(R.id.islandCard)
        val cityCard = findViewById<CardView>(R.id.cityCard)

        mountainCard.setOnClickListener {
            val intent = Intent(this, MountainBranch::class.java)
            startActivity(intent)
        }

        islandCard.setOnClickListener {
            startActivity(Intent(this, IslandBranch::class.java))
        }

        cityCard.setOnClickListener {
            startActivity(Intent(this, CityBranch::class.java))
        }

    }
}
