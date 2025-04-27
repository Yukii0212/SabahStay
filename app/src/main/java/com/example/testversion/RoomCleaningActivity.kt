package com.example.testversion

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
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
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Calendar

class RoomCleaningActivity : AppCompatActivity() {

    private lateinit var roomNumberEditText: EditText
    private lateinit var confirmButton: Button
    private var bookingId: Int = 0
    private var cleaningRequestCount: Int = 0
    private lateinit var cleaningDateEditText: EditText
    private lateinit var cleaningTimeEditText: EditText
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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

        cleaningDateEditText = findViewById(R.id.cleaningDateEditText)
        cleaningTimeEditText = findViewById(R.id.cleaningTimeEditText)

        // Show DatePickerDialog when cleaningDateEditText is clicked
        cleaningDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "${selectedDay.toString().padStart(2, '0')}/" +
                        "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                cleaningDateEditText.setText(formattedDate)
            }, year, month, day).show()
        }

        // Show TimePickerDialog when cleaningTimeEditText is clicked
        cleaningTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
                cleaningTimeEditText.setText(formattedTime)
            }, hour, minute, true).show()
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
            val serviceDao = AppDatabase.getInstance(this@RoomCleaningActivity).serviceDao()
            val currentCount = serviceDao.getCleaningRequestCount(bookingId) ?: 0
            cleaningRequestCount = currentCount + 1

            // Determine if payment is required
            val isPaymentRequired = cleaningRequestCount > 1
            val message = if (isPaymentRequired) {
                "This request will be charged RM15. Do you want to proceed?"
            } else {
                "This request is free of charge. Do you want to proceed?"
            }

            // Show confirmation dialog
            AlertDialog.Builder(this@RoomCleaningActivity)
                .setTitle("Confirm Room Cleaning Request")
                .setMessage(message)
                .setPositiveButton("Confirm") { _, _ ->
                    lifecycleScope.launch {
                        // Update cleaning request count
                        serviceDao.updateCleaningRequestCount(bookingId, cleaningRequestCount)

                        // Store the request in ServiceUsage
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

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RoomCleaningActivity,
                                if (isPaymentRequired) "You have been charged RM15 for this request." else "Your request has been processed free of charge.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
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