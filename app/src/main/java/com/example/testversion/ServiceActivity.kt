package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        // Redirect to Room Cleaning page
        findViewById<ImageView>(R.id.roomCleaning).setOnClickListener {
            navigateToPage(RoomCleaningActivity::class.java)
        }

        // Redirect to Food Ordering page
        findViewById<ImageView>(R.id.foodOrdering).setOnClickListener {
            navigateToPage(FoodOrderingActivity::class.java)
        }

        // Redirect to Laundry Service page
        findViewById<ImageView>(R.id.laundryService).setOnClickListener {
            navigateToPage(LaundryServiceActivity::class.java)
        }

        // Redirect to Taxi Booking page
        findViewById<ImageView>(R.id.taxiBooking).setOnClickListener {
            navigateToPage(TaxiBookingActivity::class.java)
        }

        // Redirect to View Bills page
        findViewById<ImageView>(R.id.viewBills).setOnClickListener {
            navigateToPage(ViewBillsActivity::class.java)
        }
    }
    private fun getCurrentUserEmail(): String? {
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        return sharedPreferences.getString("email", null)
    }

    private fun navigateToPage(activityClass: Class<*>) {
        lifecycleScope.launch {
            val bookingId = withContext(Dispatchers.IO) {
                val userEmail = getCurrentUserEmail()
                Log.d("ServiceActivity", "User email: $userEmail")

                userEmail?.let {
                    AppDatabase.getInstance(this@ServiceActivity).finalizedBookingDao()
                        .getLatestBookingIdForUser(it)
                }
            }

            Log.d("ServiceActivity", "Booking ID: $bookingId")

            if (bookingId != null) {
                val intent = Intent(this@ServiceActivity, activityClass)
                intent.putExtra("bookingId", bookingId)
                startActivity(intent)
            } else {
                Toast.makeText(this@ServiceActivity, "No active booking found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}