package com.example.testversion

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.ServiceUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class RoomCleaningActivity : AppCompatActivity() {

    private lateinit var roomNumberEditText: EditText
    private lateinit var confirmButton: Button
    private var bookingId: Int = 0
    private var cleaningRequestCount: Int = 0
    private lateinit var cleaningDateEditText: EditText
    private lateinit var cleaningTimeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_cleaning)

        roomNumberEditText = findViewById(R.id.roomNumberEditText)
        confirmButton = findViewById(R.id.submitCleaningRequestButton)

        // Get bookingId from intent
        bookingId = intent.getIntExtra("bookingId", 0)
        Log.d("RoomCleaningActivity", "Retrieved bookingId: $bookingId")

        // Set up confirm button click listener
        confirmButton.setOnClickListener {
            Log.d("RoomCleaningActivity", "Submit button clicked")
            handleRoomCleaningRequest()
        }

        cleaningDateEditText = findViewById(R.id.cleaningDateEditText)
        cleaningTimeEditText = findViewById(R.id.cleaningTimeEditText)

        // Show DatePickerDialog when cleaningDateEditText is clicked
        cleaningDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "${selectedDay.toString().padStart(2, '0')}/" +
                            "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                    cleaningDateEditText.setText(formattedDate)
                },
                year,
                month,
                day
            )
            // Set minimum date to today
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        // Show TimePickerDialog when cleaningTimeEditText is clicked
        cleaningTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    val isPM = selectedHour >= 12
                    val formattedHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                    val formattedTime = String.format(
                        "%02d:%02d %s",
                        formattedHour,
                        selectedMinute,
                        if (isPM) "PM" else "AM"
                    )
                    cleaningTimeEditText.setText(formattedTime)
                },
                hour,
                minute,
                false
            ).show()
        }

        setupKeyboardScrolling()
    }

    private fun handleRoomCleaningRequest() {
        val roomNumber = roomNumberEditText.text.toString()
        val cleaningDate = cleaningDateEditText.text.toString()
        val cleaningTime = cleaningTimeEditText.text.toString()

        // Validate inputs
        if (roomNumber.isEmpty() || cleaningDate.isEmpty() || cleaningTime.isEmpty()) {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                Log.d("RoomCleaningActivity", "Starting database operations")
                val serviceDao = AppDatabase.getInstance(this@RoomCleaningActivity).serviceDao()
                val currentCount = serviceDao.getCleaningRequestCount(bookingId) ?: 0
                cleaningRequestCount = currentCount + 1

                val isPaymentRequired = cleaningRequestCount > 1
                val message = if (isPaymentRequired) {
                    "This request will be charged RM15. Do you want to proceed?"
                } else {
                    "This request is free of charge. Do you want to proceed?"
                }

                Log.d("RoomCleaningActivity", "Showing confirmation dialog")
                AlertDialog.Builder(this@RoomCleaningActivity)
                    .setTitle("Confirm Room Cleaning Request")
                    .setMessage(message)
                    .setPositiveButton("Confirm") { _, _ ->
                        lifecycleScope.launch {
                            try {
                                Log.d("RoomCleaningActivity", "Updating cleaning request count")
                                withContext(Dispatchers.IO) {
                                    serviceDao.updateCleaningRequestCount(bookingId, cleaningRequestCount)

                                    val serviceUsage = ServiceUsage(
                                        bookingId = bookingId.toString(),
                                        roomNumber = roomNumber,
                                        serviceId = 1,
                                        serviceName = "Room Cleaning",
                                        price = if (isPaymentRequired) 15.0 else 0.0,
                                        requestTime = cleaningTime,
                                        requestDay = cleaningDate,
                                        isCanceled = false,
                                        cleaningRequestCount = cleaningRequestCount
                                    )
                                    serviceDao.insertServiceUsage(serviceUsage)
                                }

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@RoomCleaningActivity,
                                        if (isPaymentRequired) "You have been charged RM15 for this request." else "Your request has been processed free of charge.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            } catch (e: Exception) {
                                Log.e("RoomCleaningActivity", "Error in inner database operation", e)
                            }
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            } catch (e: Exception) {
                Log.e("RoomCleaningActivity", "Error in handleRoomCleaningRequest", e)
            }
        }
    }

    private fun setupKeyboardScrolling() {
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val editTextList = listOf(
            findViewById<EditText>(R.id.roomNumberEditText),
            findViewById<EditText>(R.id.guestNameEditText),
            findViewById<EditText>(R.id.cleaningDateEditText),
            findViewById<EditText>(R.id.cleaningTimeEditText)
        )

        for (editText in editTextList) {
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    scrollView.post {
                        scrollView.smoothScrollTo(0, editText.top)
                    }
                }
            }
        }
    }
}