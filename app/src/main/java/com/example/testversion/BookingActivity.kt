package com.example.testversion

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var branchSpinner: Spinner
    private lateinit var roomTypeSpinner: Spinner
    private lateinit var checkInTextView: EditText
    private lateinit var checkOutTextView: EditText
    private lateinit var roomCountTextView: EditText
    private lateinit var adultCountTextView: EditText
    private lateinit var childCountTextView: EditText
    private lateinit var addBedCheckbox: CheckBox
    private lateinit var buffetCheckbox: CheckBox
    private lateinit var costSummaryTextView: TextView
    private lateinit var confirmButton: Button

    private var basePricePerNight = 0.0
    private var numberOfNights = 0L
    private var totalCost = 0.0
    private var roomId: String? = null
    private var updatedAdults: Int = 1
    private var updatedChildren: Int = 0

    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_page)

        nameEditText = findViewById(R.id.edit_name)
        emailEditText = findViewById(R.id.edit_email)
        phoneEditText = findViewById(R.id.edit_phone)
        branchSpinner = findViewById(R.id.spinner_branch)
        roomTypeSpinner = findViewById(R.id.spinner_room_type)
        checkInTextView = findViewById(R.id.edit_check_in)
        checkOutTextView = findViewById(R.id.edit_check_out)
        roomCountTextView = findViewById(R.id.edit_room_count)
        adultCountTextView = findViewById(R.id.edit_adult_count)
        childCountTextView = findViewById(R.id.edit_child_count)
        addBedCheckbox = findViewById(R.id.checkbox_extra_bed)
        buffetCheckbox = findViewById(R.id.checkbox_buffet)
        costSummaryTextView = findViewById(R.id.text_cost_summary)
        confirmButton = findViewById(R.id.button_confirm)

        val intent = intent
        val userEmail = intent.getStringExtra("userEmail") ?: ""
        val branchName = intent.getStringExtra("branch") ?: "KK City"
        val roomType = intent.getStringExtra("roomType") ?: "Single Room"
        val roomCount = intent.getIntExtra("roomCount", 1)
        val adults = intent.getIntExtra("adults", 1)
        val children = intent.getIntExtra("children", 0)

        val checkInRaw = intent.getStringExtra("checkIn") ?: ""
        val checkOutRaw = intent.getStringExtra("checkOut") ?: ""

        adultCountTextView.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                updatedAdults = s.toString().toIntOrNull() ?: 1
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        childCountTextView.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                updatedChildren = s.toString().toIntOrNull() ?: 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        lifecycleScope.launch {
            val user = AppDatabase.getInstance(this@BookingActivity).userDao().getUserByEmail(userEmail)
            if (user == null) {
                Toast.makeText(this@BookingActivity, "User not found in database: $userEmail", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }

            nameEditText.setText(user.name)
            emailEditText.setText(user.email)
            phoneEditText.setText(user.phone)

            nameEditText.isEnabled = false
            emailEditText.isEnabled = false
            phoneEditText.isEnabled = false
        }

        try {
            val checkInDate = LocalDate.parse(checkInRaw, formatter)
            val checkOutDate = LocalDate.parse(checkOutRaw, formatter)

            checkInTextView.setText(formatter.format(checkInDate))
            checkOutTextView.setText(formatter.format(checkOutDate))

            numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate)

            setupBranchAndRoomTypeSpinners(branchName, roomType)

            roomCountTextView.setText(roomCount.toString())
            adultCountTextView.setText(adults.toString())
            childCountTextView.setText(children.toString())

            lifecycleScope.launch {
                val user = AppDatabase.getInstance(this@BookingActivity).userDao().getUserByEmail(userEmail)
                user?.let {
                    nameEditText.setText(it.name)
                    emailEditText.setText(it.email)
                    phoneEditText.setText(it.phone)
                }

                val branchId = when (branchName) {
                    "KK City" -> "kkcity"
                    "Kundasang" -> "kundasang"
                    "Island Branch" -> "island"
                    else -> "kkcity"
                }
                updatePriceAndCost()
                val room = AppDatabase.getInstance(this@BookingActivity).roomDao()
                    .findAvailableRoomBetweenDates(branchId, roomType, checkInDate, checkOutDate)

                if (room == null) {
                    Toast.makeText(this@BookingActivity, "Room not found. Please try again.", Toast.LENGTH_LONG).show()
                    return@launch
                }

                roomId = room.roomId

            }

            addBedCheckbox.setOnCheckedChangeListener { _, _ -> updatePriceAndCost() }
            buffetCheckbox.setOnCheckedChangeListener { _, _ -> updatePriceAndCost() }

            confirmButton.setOnClickListener {
                val checkInStr = checkInTextView.text.toString()
                val checkOutStr = checkOutTextView.text.toString()

                if (!isValidEmail(emailEditText.text.toString())) {
                    emailEditText.error = "Invalid email (e.g., test@gmail.com)"
                    return@setOnClickListener
                }

                if (!isValidPhone(phoneEditText.text.toString())) {
                    phoneEditText.error = "Invalid phone (e.g., 010-1234567)"
                    return@setOnClickListener
                }

                if (checkInStr.isBlank() || checkOutStr.isBlank()) {
                    Toast.makeText(this, "Please select check-in and check-out dates", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                try {
                    LocalDate.parse(checkInStr, formatter)
                    LocalDate.parse(checkOutStr, formatter)
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid date format. Please reselect the dates.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val intent = Intent(this, PaymentDetailsActivity::class.java).apply {
                    putExtra("totalCost", totalCost)
                    putExtra("basePrice", basePricePerNight)
                    putExtra("numberOfNights", numberOfNights)
                    putExtra("name", nameEditText.text.toString())
                    putExtra("userEmail", emailEditText.text.toString())
                    putExtra("phone", phoneEditText.text.toString())
                    putExtra("branch", branchSpinner.selectedItem.toString())
                    putExtra("roomType", roomTypeSpinner.selectedItem.toString())
                    putExtra("checkIn", checkInTextView.text.toString())
                    putExtra("checkOut", checkOutTextView.text.toString())
                    putExtra("roomId", roomId ?: "")
                    putExtra("numberOfAdults", updatedAdults)
                    putExtra("numberOfChildren", updatedChildren)
                    putExtra("extraBed", addBedCheckbox.isChecked)
                    putExtra("buffetAdult", updatedAdults)
                    putExtra("buffetChild", updatedChildren)
                }
                startActivity(intent)
            }

            addTextWatcher(roomCountTextView)
            addTextWatcher(adultCountTextView)
            addTextWatcher(childCountTextView)

            enforceMinimum(roomCountTextView, 1)
            enforceMinimum(adultCountTextView, 1)
            enforceMinimum(childCountTextView, 0)

            checkInTextView.setOnClickListener {
                showDatePicker { date ->
                    checkInTextView.setText(date.format(formatter))
                    updatePriceAndCost()
                }
            }
            checkOutTextView.setOnClickListener {
                showDatePicker { date ->
                    checkOutTextView.setText(date.format(formatter))
                    updatePriceAndCost()
                }
            }

        } catch (e: Exception) {
            Log.e("BookingActivity", "Date parsing failed: ${e.message}")
            Toast.makeText(this, "Invalid date format received", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun enforceMinimum(editText: EditText, minimum: Int) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val value = editText.text.toString().toIntOrNull() ?: minimum
                if (value < minimum) {
                    editText.setText(minimum.toString())
                }
            }
        }
    }

    private fun addTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) = updatePriceAndCost()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupBranchAndRoomTypeSpinners(selectedBranch: String, selectedRoomType: String) {
        val branchOptions = listOf("KK City", "Kundasang", "Island Branch")
        val roomTypeOptions = mapOf(
            "KK City" to listOf("Single Room", "Double Room", "Queen Room", "Deluxe Suite"),
            "Kundasang" to listOf("Single Room", "Double Room", "Queen Room", "Mountain View Suite"),
            "Island Branch" to listOf("Single Room", "Double Room", "Queen Room", "Beachfront Suite")
        )

        val branchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, branchOptions)
        branchSpinner.adapter = branchAdapter
        branchSpinner.setSelection(branchOptions.indexOf(selectedBranch))

        fun updateRoomTypeSpinner(branch: String) {
            val roomAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roomTypeOptions[branch] ?: emptyList())
            roomTypeSpinner.adapter = roomAdapter
            roomTypeSpinner.setSelection(roomTypeOptions[branch]?.indexOf(selectedRoomType) ?: 0)
        }

        updateRoomTypeSpinner(selectedBranch)

        branchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = branchOptions[position]
                updateRoomTypeSpinner(selected)
                updatePriceAndCost()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        roomTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) = updatePriceAndCost()
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        val today = LocalDate.now()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val date = LocalDate.of(year, month + 1, dayOfMonth)
            onDateSelected(date)
        }, today.year, today.monthValue - 1, today.dayOfMonth).show()
    }

    private fun updatePriceAndCost() {
        try {
            val checkInDate = LocalDate.parse(checkInTextView.text.toString(), formatter)
            val checkOutDate = LocalDate.parse(checkOutTextView.text.toString(), formatter)

            if (checkOutDate.isBefore(checkInDate)) return

            numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate)
            val roomCount = maxOf(roomCountTextView.text.toString().toIntOrNull() ?: 1, 1)
            val adults = maxOf(adultCountTextView.text.toString().toIntOrNull() ?: 1, 1)
            val children = maxOf(childCountTextView.text.toString().toIntOrNull() ?: 0, 0)

            val branch = branchSpinner.selectedItem.toString()
            val roomType = roomTypeSpinner.selectedItem.toString()
            basePricePerNight = getPrice(branch, roomType)

            calculateCost(roomCount, adults, children)
        } catch (e: Exception) {
            Log.e("BookingActivity", "Cost update failed: ${e.message}")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        return Regex("^(\\+?60)?1[0-9]{8,9}$").matches(phone.trim())
    }

    private fun getPrice(branch: String, roomType: String): Double {
        val priceMap = mapOf(
            "KK City" to mapOf("Single Room" to 180.0, "Double Room" to 240.0, "Queen Room" to 350.0, "Deluxe Suite" to 550.0),
            "Kundasang" to mapOf("Single Room" to 220.0, "Double Room" to 260.0, "Queen Room" to 370.0, "Mountain View Suite" to 580.0),
            "Island Branch" to mapOf("Single Room" to 250.0, "Double Room" to 320.0, "Queen Room" to 400.0, "Beachfront Suite" to 700.0)
        )
        return priceMap[branch]?.get(roomType) ?: 0.0
    }

    private fun calculateCost(roomCount: Int, adults: Int, children: Int) {
        val nights = numberOfNights.toInt()
        val roomCost = basePricePerNight * roomCount * nights
        val extraBedCost = if (addBedCheckbox.isChecked) 100.0 * nights else 0.0
        val buffetAdultCost = if (buffetCheckbox.isChecked) 120.0 * adults * nights else 0.0
        val buffetChildCost = if (buffetCheckbox.isChecked) 80.0 * children * nights else 0.0

        val subtotal = roomCost + extraBedCost + buffetAdultCost + buffetChildCost
        val tax = subtotal * 0.10
        totalCost = subtotal + tax

        val summary = """
            Room: RM%.2f x %d nights = RM%.2f
            Extra Bed: RM%.2f 
            Buffet: RM%.2f (Adults) + RM%.2f (Children)
            Tax (10%%): RM%.2f

            Total: RM%.2f
        """.trimIndent().format(
            basePricePerNight, nights, roomCost,
            extraBedCost,
            buffetAdultCost, buffetChildCost,
            tax,
            totalCost
        )

        costSummaryTextView.text = summary
    }
}