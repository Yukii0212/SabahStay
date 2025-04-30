package com.example.testversion

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.ServiceUsage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LaundryServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laundry_service)

        val roomNumberInput: EditText = findViewById(R.id.roomNumberEditText)
        val pickupDateButton: EditText = findViewById(R.id.pickupDateEditText)
        val pickupTimeButton: EditText = findViewById(R.id.pickupTimeEditText)
        val dropoffDateButton: EditText = findViewById(R.id.dropoffDateEditText)
        val dropoffTimeButton: EditText = findViewById(R.id.dropoffTimeEditText)
        val confirmButton: Button = findViewById(R.id.submitLaundryRequestButton)

        val calendar = Calendar.getInstance()

        val datePickerMinDate = calendar.timeInMillis

        pickupDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    pickupDateButton.setText("$dayOfMonth/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = datePickerMinDate
            datePickerDialog.show()
        }

        dropoffDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    dropoffDateButton.setText("$dayOfMonth/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = datePickerMinDate
            datePickerDialog.show()
        }

        pickupTimeButton.setOnClickListener {
            TimePickerDialog(this, { _, hourOfDay, minute ->
                pickupTimeButton.setText(String.format("%02d:%02d", hourOfDay, minute))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        dropoffTimeButton.setOnClickListener {
            TimePickerDialog(this, { _, hourOfDay, minute ->
                val pickupDate = pickupDateButton.text.toString()
                val pickupTime = pickupTimeButton.text.toString()
                val dropoffDate = dropoffDateButton.text.toString()

                if (pickupDate.isNotEmpty() && pickupTime.isNotEmpty() && dropoffDate.isNotEmpty()) {
                    if (!isTimeDifferenceValid(
                            pickupDate,
                            pickupTime,
                            dropoffDate,
                            String.format("%02d:%02d", hourOfDay, minute)
                        )
                    ) {
                        Toast.makeText(
                            this,
                            "Dropoff time must be at least 30 minutes after pickup time",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TimePickerDialog
                    }
                }
                dropoffTimeButton.setText(String.format("%02d:%02d", hourOfDay, minute))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        confirmButton.setOnClickListener {
            val roomNumber = roomNumberInput.text.toString()
            val pickupDate = pickupDateButton.text.toString()
            val pickupTime = pickupTimeButton.text.toString()
            val dropoffDate = dropoffDateButton.text.toString()
            val dropoffTime = dropoffTimeButton.text.toString()
            val bookingId = intent.getIntExtra("bookingId", 0)
            if (bookingId == 0) {
                Log.e("LaundryServiceActivity", "Booking ID is missing!")
            } else {
                Log.d("LaundryServiceActivity", "Retrieved bookingId: $bookingId")
            }

            if (roomNumber.isEmpty() || pickupDate.isEmpty() || pickupTime.isEmpty() || dropoffDate.isEmpty() || dropoffTime.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirm Laundry Service")
                .setMessage("You will be charged RM20 for this service. Do you want to proceed?")
                .setPositiveButton("Confirm") { _, _ ->
                    lifecycleScope.launch {
                        val database = AppDatabase.getInstance(this@LaundryServiceActivity)
                        val serviceDao = database.serviceDao()

                        val serviceUsage = ServiceUsage(
                            bookingId = bookingId.toString(),
                            roomNumber = roomNumber,
                            serviceId = 5,
                            serviceName = "Laundry Service",
                            price = 20.0,
                            requestTime = "$pickupTime - $dropoffTime",
                            requestDay = "$pickupDate to $dropoffDate",
                            isCanceled = false
                        )

                        Log.d("LaundryServiceActivity", "Preparing to insert serviceUsage: $serviceUsage")
                        val insertedId = serviceDao.insertServiceUsage(serviceUsage)
                        Log.d("LaundryServiceActivity", "Inserted serviceUsage with ID: $insertedId")

                        Toast.makeText(
                            this@LaundryServiceActivity,
                            "Laundry service booked successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun isTimeDifferenceValid(
        pickupDate: String,
        pickupTime: String,
        dropoffDate: String,
        dropoffTime: String
    ): Boolean {
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return try {
            val pickupDateTime = dateTimeFormat.parse("$pickupDate $pickupTime")
            val dropoffDateTime = dateTimeFormat.parse("$dropoffDate $dropoffTime")

            val differenceInMillis = dropoffDateTime.time - pickupDateTime.time
            val differenceInMinutes = differenceInMillis / (1000 * 60)

            differenceInMinutes >= 30
        } catch (e: Exception) {
            Log.e("LaundryServiceActivity", "Error parsing date/time", e)
            false
        }
    }
}