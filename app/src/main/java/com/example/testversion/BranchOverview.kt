package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class BranchOverview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch_overview)

        val paymentButton = findViewById<Button>(R.id.paymentButton)
        val bookingButton = findViewById<Button>(R.id.bookingButton)

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
