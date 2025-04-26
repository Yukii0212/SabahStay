package com.example.testversion

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.runBlocking

class RoomCleaningActivity : AppCompatActivity() {

    private lateinit var roomNumberEditText: EditText
    private lateinit var confirmButton: Button
    private var bookingId: Int = 0
    private var cleaningRequestCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_cleaning)

        roomNumberEditText = findViewById(R.id.roomNumberEditText)
        confirmButton = findViewById(R.id.submitCleaningRequestButton)

        // Get bookingId from intent
        bookingId = intent.getIntExtra("bookingId", 0)

        // Set up confirm button click listener
        confirmButton.setOnClickListener {
            handleRoomCleaningRequest()
        }
    }

    private fun handleRoomCleaningRequest() {
        val roomNumber = roomNumberEditText.text.toString()
        if (roomNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a valid room number", Toast.LENGTH_SHORT).show()
            return
        }

        // Increment the cleaning request count
        cleaningRequestCount++

        // Determine if payment is required
        val isPaymentRequired = cleaningRequestCount > 1
        val message = if (isPaymentRequired) {
            "This request will be charged RM15. Do you want to proceed?"
        } else {
            "This request is free of charge. Do you want to proceed?"
        }

        // Show confirmation dialog
        AlertDialog.Builder(this)
            .setTitle("Confirm Room Cleaning Request")
            .setMessage(message)
            .setPositiveButton("Confirm") { _, _ ->
                runBlocking {
                    updateCleaningRequestCount(bookingId, cleaningRequestCount)
                }
                Toast.makeText(
                    this,
                    if (isPaymentRequired) "You have been charged RM15 for this request." else "Your request has been processed free of charge.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private suspend fun updateCleaningRequestCount(bookingId: Int, count: Int) {
        val serviceDao = AppDatabase.getInstance(this).serviceDao()
        serviceDao.updateCleaningRequestCount(bookingId, count)
    }
}