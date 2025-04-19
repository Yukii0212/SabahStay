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

class SearchAvailableRoomActivity : AppCompatActivity() {

    private lateinit var checkInEditText: EditText
    private lateinit var checkOutEditText: EditText
    private lateinit var roomGuestEditText: EditText
    private lateinit var searchButton: Button

    private var checkInDateMillis: Long? = null
    private var checkOutDateMillis: Long? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_available_room)

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
                val db = AppDatabase.getInstance(this@SearchAvailableRoomActivity)
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
                    Toast.makeText(this@SearchAvailableRoomActivity, "${availableRooms.size} rooms available", Toast.LENGTH_SHORT).show()
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
            val db = AppDatabase.getInstance(this@SearchAvailableRoomActivity)
            val roomDao = db.roomDao()
            val branchDao = db.branchDao()
            val bookingDao = db.bookingDao()
            val userDao = db.userDao()

            val branchId = "branch-excel"
            val userEmail = "tester@excel.com"

            // 1. Insert test branch
            branchDao.insert(
                Branch(
                    branchId = branchId,
                    name = "Excel Island Branch",
                    location = "Island Paradise",
                    contactNumber = "0000000000",
                    email = "excel@branch.com"
                )
            )

            // 2. Insert test user
            userDao.insert(
                User(
                    email = userEmail,
                    name = "Excel Tester",
                    passport = "T1234567",
                    gender = "Other",
                    phone = "0000000000",
                    password = "test123"
                )
            )

            // 3. Insert 5 test rooms (slot 1 to slot 5)
            for (i in 1..5) {
                roomDao.insert(
                    HotelRoom(
                        roomId = "room-$i",
                        roomNumber = "10$i",
                        roomType = "Standard",
                        pricePerNight = 100.0 + i,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Slot $i test room",
                        maxGuests = 2,
                        branchId = branchId
                    )
                )
            }

            // 4. Insert bookings based on your Excel test table
            val bookings = listOf(
                // Slot 1: Yuki booking from 18 to 20 April
                Booking(
                    bookingId = "b1",
                    userEmail = userEmail,
                    roomId = "room-1",
                    checkInDate = LocalDate.parse("2025-04-18"),
                    checkOutDate = LocalDate.parse("2025-04-20"),
                    totalPrice = 200.0,
                    status = "confirmed",
                    numGuests = 2,
                    createdAt = LocalDate.now(),
                    paymentStatus = "paid"
                ),

                // Slot 2: Pei Pei booking from 17 to 19 April
                Booking(
                    bookingId = "b2",
                    userEmail = userEmail,
                    roomId = "room-2",
                    checkInDate = LocalDate.parse("2025-04-17"),
                    checkOutDate = LocalDate.parse("2025-04-19"),
                    totalPrice = 200.0,
                    status = "confirmed",
                    numGuests = 2,
                    createdAt = LocalDate.now(),
                    paymentStatus = "paid"
                ),

                // Slot 3: Wei Wei booking from 15 to 18 April
                Booking(
                    bookingId = "b3",
                    userEmail = userEmail,
                    roomId = "room-3",
                    checkInDate = LocalDate.parse("2025-04-15"),
                    checkOutDate = LocalDate.parse("2025-04-18"),
                    totalPrice = 300.0,
                    status = "confirmed",
                    numGuests = 2,
                    createdAt = LocalDate.now(),
                    paymentStatus = "paid"
                ),

                // Slot 4: Gabriel booking from 16 to 21 April
                Booking(
                    bookingId = "b4",
                    userEmail = userEmail,
                    roomId = "room-4",
                    checkInDate = LocalDate.parse("2025-04-16"),
                    checkOutDate = LocalDate.parse("2025-04-21"),
                    totalPrice = 500.0,
                    status = "confirmed",
                    numGuests = 2,
                    createdAt = LocalDate.now(),
                    paymentStatus = "paid"
                ),

                // Slot 5: Rachel booking from 16 to 19 April
                Booking(
                    bookingId = "b5",
                    userEmail = userEmail,
                    roomId = "room-5",
                    checkInDate = LocalDate.parse("2025-04-16"),
                    checkOutDate = LocalDate.parse("2025-04-19"),
                    totalPrice = 300.0,
                    status = "confirmed",
                    numGuests = 2,
                    createdAt = LocalDate.now(),
                    paymentStatus = "paid"
                )
            )

            bookings.forEach { bookingDao.insert(it) }

            Toast.makeText(this@SearchAvailableRoomActivity, "🌴 Excel test data inserted successfully!", Toast.LENGTH_LONG).show()
        }
    }

}