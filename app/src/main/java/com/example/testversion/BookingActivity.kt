// File: BookingActivity.kt
package com.example.testversion

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var checkInEditText: EditText
    private lateinit var checkOutEditText: EditText
    private lateinit var roomGuestEditText: EditText
    private lateinit var searchButton: Button

    private var checkInDateMillis: Long? = null
    private var checkOutDateMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_page)

        checkInEditText = findViewById(R.id.edit_check_in)
        checkOutEditText = findViewById(R.id.edit_check_out)
        roomGuestEditText = findViewById(R.id.edit_room_guest)
        searchButton = findViewById(R.id.button_search)

        checkInEditText.setOnClickListener { showDatePicker(true) }
        checkOutEditText.setOnClickListener { showDatePicker(false) }
        roomGuestEditText.setOnClickListener { showRoomGuestDialog() }

        searchButton.setOnClickListener {
            if (checkInDateMillis != null && checkOutDateMillis != null && checkInDateMillis!! >= checkOutDateMillis!!) {
                Toast.makeText(this, "Check-out date must be after Check-in date", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(isCheckIn: Boolean) {
        val calendar = Calendar.getInstance()

        if (!isCheckIn && checkInDateMillis == null) {
            Toast.makeText(this, "Please select a check-in date first", Toast.LENGTH_SHORT).show()
            return
        }

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val selectedDate = selectedCalendar.timeInMillis
                val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate))

                if (isCheckIn) {
                    checkInEditText.setText(formattedDate)
                    checkInDateMillis = selectedDate
                } else {
                    checkOutEditText.setText(formattedDate)
                    checkOutDateMillis = selectedDate
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        if (isCheckIn) {
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        } else {
            datePicker.datePicker.minDate = checkInDateMillis!! + 86400000
        }

        datePicker.show()
    }


    private fun showRoomGuestDialog() {
        val dialogView = this.layoutInflater.inflate(R.layout.dialog_room_guest, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val roomMinus = dialogView.findViewById<Button>(R.id.button_room_minus)
        val roomPlus = dialogView.findViewById<Button>(R.id.button_room_plus)
        val adultMinus = dialogView.findViewById<Button>(R.id.button_adult_minus)
        val adultPlus = dialogView.findViewById<Button>(R.id.button_adult_plus)
        val childMinus = dialogView.findViewById<Button>(R.id.button_child_minus)
        val childPlus = dialogView.findViewById<Button>(R.id.button_child_plus)

        val roomCount = dialogView.findViewById<TextView>(R.id.text_room_count)
        val adultCount = dialogView.findViewById<TextView>(R.id.text_adult_count)
        val childCount = dialogView.findViewById<TextView>(R.id.text_child_count)

        val applyButton = dialogView.findViewById<Button>(R.id.button_apply)

        roomCount.text = "0"
        adultCount.text = "0"
        childCount.text = "0"

        roomMinus.setOnClickListener {
            val value = roomCount.text.toString().toInt()
            if (value > 0) roomCount.text = (value - 1).toString()
        }
        roomPlus.setOnClickListener {
            val value = roomCount.text.toString().toInt()
            roomCount.text = (value + 1).toString()
        }
        adultMinus.setOnClickListener {
            val value = adultCount.text.toString().toInt()
            if (value > 0) adultCount.text = (value - 1).toString()
        }
        adultPlus.setOnClickListener {
            val value = adultCount.text.toString().toInt()
            adultCount.text = (value + 1).toString()
        }
        childMinus.setOnClickListener {
            val value = childCount.text.toString().toInt()
            if (value > 0) childCount.text = (value - 1).toString()
        }
        childPlus.setOnClickListener {
            val value = childCount.text.toString().toInt()
            childCount.text = (value + 1).toString()
        }

        applyButton.setOnClickListener {
            val room = roomCount.text.toString().toInt()
            val adult = adultCount.text.toString().toInt()
            val child = childCount.text.toString().toInt()

            if (room < 1 || adult < 1) {
                Toast.makeText(this, "Please select at least 1 Room and 1 Adult", Toast.LENGTH_SHORT).show()
            } else {
                val result = "$room Room, $adult Adult, $child Child"
                roomGuestEditText.setText(result)
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}
