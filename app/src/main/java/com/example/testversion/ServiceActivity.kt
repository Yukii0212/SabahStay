package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class ServiceActivity : AppCompatActivity() {

    private var selectedBookingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        val selectedBookingTextView = findViewById<TextView>(R.id.selectedBookingTextView)

        lifecycleScope.launch {
            val bookings = withContext(Dispatchers.IO) {
                val userEmail = getCurrentUserEmail()
                Log.d("ServiceActivity", "User email: $userEmail")

                userEmail?.let {
                    AppDatabase.getInstance(this@ServiceActivity).finalizedBookingDao()
                        .getActiveBookingsForUser(it, LocalDate.now())
                } ?: emptyList()
            }

            if (bookings.isEmpty()) {
                Toast.makeText(this@ServiceActivity, "No active booking found", Toast.LENGTH_SHORT).show()
                finish()
            } else if (bookings.size == 1) {
                selectedBookingId = bookings[0].bookingNumber
                selectedBookingTextView.text = "Selected Booking ID: $selectedBookingId"
            } else {
                val bookingNumbers = bookings.map { "Booking ID: ${it.bookingNumber}, Branch: ${it.branchName}" }

                val dialogView = layoutInflater.inflate(R.layout.dialog_booking_selection, null)
                val listView = dialogView.findViewById<ListView>(R.id.bookingListView)

                val adapter = ArrayAdapter<String>(this@ServiceActivity, android.R.layout.simple_list_item_1, bookingNumbers)
                listView.adapter = adapter

                val builder = AlertDialog.Builder(this@ServiceActivity)
                builder.setTitle("Select a Booking")
                builder.setView(dialogView)
                val dialog = builder.create()

                listView.setOnItemClickListener { _, _, which, _ ->
                    selectedBookingId = bookings[which].bookingNumber
                    selectedBookingTextView.text = "Selected Booking ID: $selectedBookingId"
                    dialog.dismiss()
                }

                val displayMetrics = resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels
                val screenHeight = displayMetrics.heightPixels

                dialog.show()
                dialog.window?.setLayout((screenWidth * 0.9).toInt(), (screenHeight * 0.7).toInt())
            }
        }

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
        if (selectedBookingId == null) {
            Toast.makeText(this, "No booking selected", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, activityClass)
        intent.putExtra("bookingId", selectedBookingId?.toInt())
        startActivity(intent)
    }
}