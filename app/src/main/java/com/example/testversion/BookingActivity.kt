// File: BookingActivity.kt
package com.example.testversion

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.Booking
import com.example.testversion.database.Branch
import com.example.testversion.database.HotelRoom
import com.example.testversion.database.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var checkInEditText: EditText
    private lateinit var checkOutEditText: EditText
    private lateinit var roomGuestEditText: EditText
    private lateinit var searchButton: Button

    private var checkInDateMillis: Long? = null
    private var checkOutDateMillis: Long? = null

    @RequiresApi(Build.VERSION_CODES.O)
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
            if (checkInDateMillis == null || checkOutDateMillis == null) {
                Toast.makeText(this, "Please select both check-in and check-out dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (checkInDateMillis!! >= checkOutDateMillis!!) {
                Toast.makeText(this, "Check-out date must be after Check-in date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val db = AppDatabase.getInstance(this@BookingActivity)
                val roomDao = db.roomDao()
                val bookingDao = db.bookingDao()

                val checkIn = Instant.ofEpochMilli(checkInDateMillis!!)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val checkOut = Instant.ofEpochMilli(checkOutDateMillis!!)
                    .atZone(ZoneId.systemDefault()).toLocalDate()

                val allRooms = roomDao.getAll()
                val availableRooms = mutableListOf<HotelRoom>()
                val unavailableDates = mutableSetOf<LocalDate>()

                for (room in allRooms) {
                    if (!room.isAvailable) continue

                    val conflicts = bookingDao.getConflictingBookings(room.roomId, checkIn, checkOut)

                    if (conflicts.isEmpty()) {
                        availableRooms.add(room)
                    } else {
                        conflicts.forEach { conflict ->
                            var date = conflict.checkInDate
                            while (date.isBefore(conflict.checkOutDate)) {
                                unavailableDates.add(date)
                                date = date.plusDays(1)
                            }
                        }
                    }
                }

                if (availableRooms.isEmpty()) {
                    showUnavailableDatesDialog(unavailableDates)
                } else {
                    Toast.makeText(this@BookingActivity, "${availableRooms.size} rooms available", Toast.LENGTH_SHORT).show()
                }
            }
        }
        insertTestRoomData()
    }

    private fun showUnavailableDatesDialog(conflictingDates: Set<LocalDate>) {
        if (conflictingDates.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("No Rooms Available")
                .setMessage("No rooms are available for the selected dates.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")
        val sortedConflicts = conflictingDates.sorted()

        val unavailableNights = sortedConflicts.joinToString("\n") {
            "• ${it.format(formatter)}"
        }

        val nextCheckIn = sortedConflicts.last().plusDays(1).format(formatter)
        val latestCheckout = sortedConflicts.first().format(formatter)

        val userCheckIn = Instant.ofEpochMilli(checkInDateMillis!!)
            .atZone(ZoneId.systemDefault()).toLocalDate()
        val userCheckOut = Instant.ofEpochMilli(checkOutDateMillis!!)
            .atZone(ZoneId.systemDefault()).toLocalDate()

        val userCheckInBlocked = conflictingDates.contains(userCheckIn)
        val userCheckOutBlocked = conflictingDates.contains(userCheckOut.minusDays(1))

        val message = buildString {
            appendLine("❌ You cannot stay on these nights:")
            appendLine(unavailableNights)
            appendLine()

            // 🔁 Message priority based on which part of user's input is invalid
            if (userCheckOutBlocked) {
                appendLine("✅ You may check out on:")
                appendLine("• $latestCheckout")
                appendLine()
            }

            if (userCheckInBlocked) {
                appendLine("✅ Next available check-in:")
                appendLine("• $nextCheckIn")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Unavailable Stay Dates")
            .setMessage(message.trim())
            .setPositiveButton("OK", null)
            .show()
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

    private fun insertTestRoomData() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@BookingActivity)
            val roomDao = db.roomDao()
            val branchDao = db.branchDao()
            val bookingDao = db.bookingDao()
            val userDao = db.userDao()

            val testBranchId = "test-branch-001"
            val testRoomId = "test-room-001"
            val testUserEmail = "dummy@test.com"

            branchDao.insert(
                Branch(
                    branchId = testBranchId,
                    name = "Test Branch",
                    location = "Test City",
                    contactNumber = "123456789",
                    email = "test@branch.com"
                )
            )

            roomDao.insert(
                HotelRoom(
                    roomId = testRoomId,
                    roomNumber = "101",
                    roomType = "Standard",
                    pricePerNight = 99.99,
                    isAvailable = true,
                    bedCount = 1,
                    description = "Test Room for April 18–19 only",
                    maxGuests = 2,
                    branchId = testBranchId
                )
            )

            userDao.insert(
                User(
                    email = testUserEmail,
                    name = "Test User",
                    passport = "12345678",
                    gender = "Other",
                    phone = "1234567890",
                    password = "test"
                )
            )

            // Add a conflicting booking on April 20 to block all other dates
            bookingDao.insert(
                Booking(
                    bookingId = "test-booking-001",
                    userEmail = testUserEmail,
                    roomId = testRoomId,
                    checkInDate = LocalDate.parse("2025-04-20"),
                    checkOutDate = LocalDate.parse("2025-04-22"),
                    totalPrice = 199.99,
                    status = "confirmed",
                    numGuests = 2,
                    createdAt = LocalDate.now(),
                    paymentStatus = "paid"
                )
            )

            Toast.makeText(this@BookingActivity, "Test room + dummy booking inserted", Toast.LENGTH_SHORT).show()
        }
    }
}
