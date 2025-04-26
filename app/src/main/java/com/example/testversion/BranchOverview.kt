package com.example.testversion

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BranchOverview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch_overview)

        val paymentButton = findViewById<Button>(R.id.paymentButton)
        val bookingButton = findViewById<Button>(R.id.bookingButton)

        paymentButton.setOnClickListener {
            lifecycleScope.launch {
                val database = AppDatabase.getInstance(applicationContext)
                val bookings = withContext(Dispatchers.IO) {
                    database.finalizedBookingDao().getAllBookings()
                }
                val count = bookings.size
                Toast.makeText(this@BranchOverview, "$count bookings in the system", Toast.LENGTH_SHORT).show()
            }
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
