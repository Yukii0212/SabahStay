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
import com.example.testversion.database.HotelRoom
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import android.content.Intent
import android.widget.ImageView
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class SearchAvailableRoomActivity : AppCompatActivity() {

    private lateinit var checkInEditText: EditText
    private lateinit var checkOutEditText: EditText
    private lateinit var roomGuestEditText: EditText
    private lateinit var roomTypeEditText: EditText
    private lateinit var branchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var bookPromptButton: Button

    private var checkInDateMillis: Long? = null
    private var checkOutDateMillis: Long? = null
    private var selectedBranchId: String? = null
    private var selectedRoomType: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_available_room)

        //Handle auto-fill of branch and room type
        val passedBranchName = intent.getStringExtra("branchName")
        val passedRoomType = intent.getStringExtra("roomType")

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (!passedBranchName.isNullOrEmpty()) {
            val branchEdit = findViewById<EditText>(R.id.edit_branch)
            branchEdit.setText(passedBranchName)

            lifecycleScope.launch {
                val db = AppDatabase.getInstance(this@SearchAvailableRoomActivity)
                val branches = db.branchDao().getAllBranches()

                val matchedBranch = branches.find { it.name == passedBranchName }
                selectedBranchId = matchedBranch?.branchId ?: "kkcity"
            }
        }

        if (!passedRoomType.isNullOrEmpty()) {
            val roomTypeEdit = findViewById<EditText>(R.id.edit_room_type)
            roomTypeEdit.setText(passedRoomType)
            selectedRoomType = passedRoomType
        }

        val userEmail = intent.getStringExtra("userEmail")
        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "User email is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        checkInEditText = findViewById(R.id.edit_check_in)
        checkOutEditText = findViewById(R.id.edit_check_out)
        roomGuestEditText = findViewById(R.id.edit_room_guest)
        roomTypeEditText = findViewById(R.id.edit_room_type)
        branchEditText = findViewById(R.id.edit_branch)
        searchButton = findViewById(R.id.button_search)

        selectedBranchId = intent.getStringExtra("branchId")

        checkInEditText.setOnClickListener { showDatePicker(true) }
        checkOutEditText.setOnClickListener { showDatePicker(false) }
        roomGuestEditText.setOnClickListener { showRoomGuestDialog() }
        roomTypeEditText.setOnClickListener { showRoomTypeDialog() }
        branchEditText.setOnClickListener { showBranchDialog() }

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
                val finalizedBookingDao = db.finalizedBookingDao()

                val checkIn = org.threeten.bp.Instant.ofEpochMilli(checkInDateMillis!!)
                    .atZone(org.threeten.bp.ZoneId.systemDefault()).toLocalDate()
                val checkOut = org.threeten.bp.Instant.ofEpochMilli(checkOutDateMillis!!)
                    .atZone(org.threeten.bp.ZoneId.systemDefault()).toLocalDate()

                // Get all rooms of the selected type in the branch
                val allRooms = roomDao.getByBranch(selectedBranchId!!).filter { it.roomType == selectedRoomType }

                // Calculate the total number of rooms booked (finalized and live bookings)
                val totalBookedRooms = allRooms.sumOf { room ->
                    val finalizedConflicts = finalizedBookingDao.getConflictingFinalizedBookings(room.roomId, checkIn, checkOut).size
                    val liveConflicts = bookingDao.getConflictingBookings(room.roomId, checkIn, checkOut).size
                    finalizedConflicts + liveConflicts
                }

                // Calculate the number of available rooms
                val availableRoomsCount = allRooms.size - totalBookedRooms

                if (availableRoomsCount <= 0) {
                    Toast.makeText(this@SearchAvailableRoomActivity, "No rooms available for the selected dates.", Toast.LENGTH_LONG).show()
                    return@launch
                }

                AlertDialog.Builder(this@SearchAvailableRoomActivity)
                    .setTitle("Rooms Available")
                    .setMessage("$availableRoomsCount room(s) are available for your selected dates.")
                    .setPositiveButton("OK") { _, _ ->
                        showBookingPromptDialog(availableRoomsCount, userEmail)
                    }
                    .show()
            }
        }
    }


    private fun parseRoomGuestInfo(info: String): Triple<Int, Int, Int> {
        val regex = Regex("""(\d+)\s+Room.*?(\d+)\s+Adult.*?(\d+)\s+Child""")
        val match = regex.find(info)
        return if (match != null) {
            val (room, adult, child) = match.destructured
            Triple(room.toInt(), adult.toInt(), child.toInt())
        } else {
            Triple(1, 1, 0)
        }
    }

    private fun showBookingPromptDialog(availableCount: Int, userEmail: String) {
        val (roomCount, adults, children) = parseRoomGuestInfo(roomGuestEditText.text.toString())

        AlertDialog.Builder(this)
            .setTitle("Book Now?")
            .setMessage("Do you want to book 1 of the $availableCount available rooms?")
            .setPositiveButton("Continue for Booking") { _, _ ->
                val intent = Intent(this, BookingActivity::class.java).apply {
                    putExtra("userEmail", userEmail)
                    putExtra("branch", branchEditText.text.toString())
                    putExtra("roomType", selectedRoomType)
                    putExtra("checkIn", checkInEditText.text.toString())
                    putExtra("checkOut", checkOutEditText.text.toString())
                    putExtra("roomCount", roomCount)
                    putExtra("adults", adults)
                    putExtra("children", children)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun showRoomTypeDialog() {
        lifecycleScope.launch {
            if (selectedBranchId.isNullOrEmpty()) {
                Toast.makeText(this@SearchAvailableRoomActivity, "Please select a branch first", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val db = AppDatabase.getInstance(this@SearchAvailableRoomActivity)
            val roomsInBranch = db.roomDao().getByBranch(selectedBranchId!!)

            val roomTypes = roomsInBranch
                .map { it.roomType }
                .distinct()

            if (roomTypes.isEmpty()) {
                Toast.makeText(this@SearchAvailableRoomActivity, "No room types available for selected branch", Toast.LENGTH_SHORT).show()
                return@launch
            }

            AlertDialog.Builder(this@SearchAvailableRoomActivity)
                .setTitle("Select Room Type")
                .setSingleChoiceItems(roomTypes.toTypedArray(), roomTypes.indexOf(selectedRoomType)) { dialog, which ->
                    selectedRoomType = roomTypes[which]
                    roomTypeEditText.setText(selectedRoomType)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }


    private fun showBranchDialog() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@SearchAvailableRoomActivity)
            val branches = db.branchDao().getAllBranches()

            if (branches.isEmpty()) {
                Toast.makeText(this@SearchAvailableRoomActivity, "No branches available", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val branchNames = branches.map { it.name }

            AlertDialog.Builder(this@SearchAvailableRoomActivity)
                .setTitle("Select Branch")
                .setSingleChoiceItems(branchNames.toTypedArray(), -1) { dialog, which ->
                    val selectedBranch = branches[which]
                    selectedBranchId = selectedBranch.branchId
                    branchEditText.setText(selectedBranch.name)
                    selectedRoomType = null // reset roomType
                    roomTypeEditText.setText("")
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
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
            appendLine("You cannot stay on these nights:")
            appendLine(unavailableNights)
            appendLine()


            if (userCheckOutBlocked) {
                appendLine("You may check out on:")
                appendLine("• $latestCheckout")
                appendLine()
            }

            if (userCheckInBlocked) {
                appendLine("Next available check-in:")
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


}