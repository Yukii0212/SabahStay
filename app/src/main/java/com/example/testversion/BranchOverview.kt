package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

        // Click listeners for the 3 cards
        val mountainCard = findViewById<CardView>(R.id.mountainCard)
        val islandCard = findViewById<CardView>(R.id.islandCard)
        val cityCard = findViewById<CardView>(R.id.cityCard)

        mountainCard.setOnClickListener {
            startActivity(Intent(this, MountainBranch::class.java))
        }

        islandCard.setOnClickListener {
            startActivity(Intent(this, IslandBranch::class.java))
        }

        cityCard.setOnClickListener {
            startActivity(Intent(this, CityBranch::class.java))
        }
    }
}
