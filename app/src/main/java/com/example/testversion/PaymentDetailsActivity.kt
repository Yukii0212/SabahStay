package com.example.testversion

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class PaymentDetailsActivity : AppCompatActivity() {
    private lateinit var paymentOptionsLayout: ConstraintLayout
    private lateinit var creditCardForm: ConstraintLayout
    private lateinit var confirmButton: Button
    private lateinit var cardNumberEditText: EditText
    private lateinit var expiryDateEditText: EditText
    private lateinit var centerHourglass: ImageView
    private lateinit var rightIcon: ImageView
    private var selectedPaymentMethod: String = ""

    private lateinit var userEmail: String
    private lateinit var roomId: String
    private lateinit var checkInDate: LocalDate
    private lateinit var checkOutDate: LocalDate
    private var extraBed: Boolean = false
    private var buffetAdult: Int = 0
    private var buffetChild: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        setContentView(R.layout.activity_payment_details)

        paymentOptionsLayout = findViewById(R.id.paymentOptionsLayout)
        creditCardForm = findViewById(R.id.creditCardForm)
        confirmButton = findViewById(R.id.confirmButton)
        cardNumberEditText = findViewById(R.id.cardNumber)
        expiryDateEditText = findViewById(R.id.expiryDate)
        centerHourglass = findViewById(R.id.centerHourglass)
        rightIcon = findViewById(R.id.rightIcon)

        val creditCardLayout = findViewById<LinearLayout>(R.id.creditCardLayout)
        val onlineBankingLayout = findViewById<LinearLayout>(R.id.onlineBankingLayout)
        val eWalletLayout = findViewById<LinearLayout>(R.id.eWalletLayout)

        confirmButton.visibility = View.GONE

        // Safely extract and parse intent data
        userEmail = intent.getStringExtra("userEmail") ?: ""
        roomId = intent.getStringExtra("roomId") ?: ""
        extraBed = intent.getBooleanExtra("extraBed", false)
        buffetAdult = intent.getIntExtra("buffetAdult", 0)
        buffetChild = intent.getIntExtra("buffetChild", 0)

        val checkInStr = intent.getStringExtra("checkIn")
        val checkOutStr = intent.getStringExtra("checkOut")

        if (checkInStr == null || checkOutStr == null) {
            Toast.makeText(this, "Missing booking dates. Returning.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        try {
            checkInDate = LocalDate.parse(checkInStr, formatter)
            checkOutDate = LocalDate.parse(checkOutStr, formatter)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        creditCardLayout.setOnClickListener {
            selectedPaymentMethod = "credit_card"
            showCreditCardForm()
        }

        onlineBankingLayout.setOnClickListener {
            selectedPaymentMethod = "online_banking"
            confirmButton.visibility = View.VISIBLE
            confirmButton.setOnClickListener { finalizeBooking() }
        }

        eWalletLayout.setOnClickListener {
            selectedPaymentMethod = "e_wallet"
            confirmButton.visibility = View.VISIBLE
            confirmButton.setOnClickListener { finalizeBooking() }
        }

        confirmButton.setOnClickListener {
            if (selectedPaymentMethod == "credit_card") {
                validateAndProceed()
            } else {
                finalizeBooking()
            }
        }

        setupCardNumberFormatter()
        setupExpiryDateFormatter()
        setupKeyboardScrolling()
    }

    private fun finalizeBooking() {
        confirmButton.text = "Processing..."
        confirmButton.isEnabled = false

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            val userDao = db.userDao()
            val roomDao = db.roomDao()
            val bookingDao = db.finalizedBookingDao()
            val user = userDao.getUserByEmail(userEmail)

            if (user == null) {
                runOnUiThread {
                    Toast.makeText(this@PaymentDetailsActivity, "❌ User not found in database: $userEmail", Toast.LENGTH_LONG).show()
                }
                runOnUiThread {
                    confirmButton.text = "Confirm Payment"
                    confirmButton.isEnabled = true
                }
                return@launch
            }

            val room = roomDao.getAll().find { it.roomId == roomId }
            if (room == null) {
                runOnUiThread {
                    confirmButton.text = "Confirm Payment"
                    confirmButton.isEnabled = true
                    Toast.makeText(this@PaymentDetailsActivity, "❌ Room not found in database: $roomId", Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            if (room == null) {
                runOnUiThread {
                    confirmButton.text = "Confirm Payment"
                    confirmButton.isEnabled = true
                    Toast.makeText(this@PaymentDetailsActivity, "Room not found. Please try again.", Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            val branch = db.branchDao().getAll().find { it.branchId == room.branchId }
            if (branch == null) {
                runOnUiThread {
                    confirmButton.text = "Confirm Payment"
                    confirmButton.isEnabled = true
                    Toast.makeText(this@PaymentDetailsActivity, "Branch not found. Please try again.", Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            val nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate).toInt()
            val basePrice = room.pricePerNight * nights
            val extraBedCost = if (extraBed) 100.0 else 0.0
            val buffetCost = buffetAdult * 120.0 + buffetChild * 80.0
            val subtotal = basePrice + extraBedCost + buffetCost
            val tax = subtotal * 0.10
            val total = subtotal + tax

            val range = when (branch.name) {
                "KK City" -> 10_000_000L to 30_000_000L
                "Kundasang" -> 40_000_000L to 60_000_000L
                else -> 70_000_000L to 90_000_000L
            }

            val lastNum = withContext(Dispatchers.IO) {
                bookingDao.getMaxBookingNumberInRange(range.first, range.second) ?: (range.first - 1)
            }

            val bookingNumber = lastNum + 1

            val finalized = FinalizedBooking(
                bookingNumber = bookingNumber,
                userEmail = userEmail,
                roomId = roomId,
                roomType = room.roomType,
                branchName = branch.name,
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
                nights = nights,
                basePrice = basePrice,
                extraBed = extraBed,
                lunchBuffetAdult = buffetAdult,
                lunchBuffetChild = buffetChild,
                tax = tax,
                totalPrice = total,
                paymentMethod = selectedPaymentMethod,
                createdAt = LocalDate.now()
            )

            bookingDao.insert(finalized)
            roomDao.insert(room.copy(isAvailable = false))

            val intent = Intent(this@PaymentDetailsActivity, BookingSuccessActivity::class.java)
            intent.putExtra("bookingNumber", bookingNumber)
            intent.putExtra("totalPrice", total)
            startActivity(intent)
            finish()
        }
    }


    private fun validateAndProceed() {
        val cardNumber = cardNumberEditText.text.toString().replace(" ", "")
        val cardHolderName = findViewById<EditText>(R.id.cardHolderName).text.toString()
        val expiryDate = expiryDateEditText.text.toString()
        val cvv = findViewById<EditText>(R.id.cvv).text.toString()

        if (cardNumber.isBlank() || cardHolderName.isBlank() || expiryDate.isBlank() || cvv.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        } else if (cardNumber.length != 16) {
            Toast.makeText(this, "Card number must be 16 digits", Toast.LENGTH_SHORT).show()
            return
        } else if (!expiryDate.matches(Regex("\\d{2}/\\d{2}"))) {
            Toast.makeText(this, "Expiry date must be MM/YY format", Toast.LENGTH_SHORT).show()
            return
        } else if (isExpired(expiryDate)) {
            startActivity(Intent(this, PaymentFailedActivity::class.java))
            return
        } else if (cvv.length !in 3..4) {
            Toast.makeText(this, "CVV must be 3 or 4 digits", Toast.LENGTH_SHORT).show()
            return
        }

        animateProgressBar()
        finalizeBooking()
    }

    private fun animateProgressBar() {
        centerHourglass.visibility = View.VISIBLE
        val rotation = ObjectAnimator.ofFloat(centerHourglass, View.ROTATION, 0f, 360f)
        rotation.duration = 1000
        rotation.repeatCount = ObjectAnimator.INFINITE
        rotation.interpolator = DecelerateInterpolator()
        rotation.start()
    }

    private fun isExpired(expiryDate: String): Boolean {
        val parts = expiryDate.split("/")
        if (parts.size != 2) return true

        val expMonth = parts[0].toIntOrNull() ?: return true
        val expYear = parts[1].toIntOrNull() ?: return true

        if (expMonth !in 1..12) return true

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        return expYear + 2000 < currentYear || (expYear + 2000 == currentYear && expMonth < currentMonth)
    }

    private fun showCreditCardForm() {
        findViewById<LinearLayout>(R.id.creditCardLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.onlineBankingLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.eWalletLayout).visibility = View.GONE

        val selectedPaymentMethodLayout = findViewById<LinearLayout>(R.id.selectedPaymentMethodLayout)
        selectedPaymentMethodLayout.visibility = View.VISIBLE

        val selectedPaymentText = findViewById<TextView>(R.id.selectedPaymentText)
        selectedPaymentText.text = "Credit Card"

        creditCardForm.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(500).setInterpolator(DecelerateInterpolator()).start()
        }

        confirmButton.visibility = View.VISIBLE
    }

    private fun setupCardNumberFormatter() {
        cardNumberEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true
                val formatted = s.toString().replace(" ", "").chunked(4).joinToString(" ")
                cardNumberEditText.setText(formatted)
                cardNumberEditText.setSelection(formatted.length)
                isFormatting = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupExpiryDateFormatter() {
        expiryDateEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true
                val clean = s.toString().replace("/", "")
                val formatted = when {
                    clean.length >= 3 -> clean.substring(0, 2) + "/" + clean.substring(2)
                    else -> clean
                }
                expiryDateEditText.setText(formatted)
                expiryDateEditText.setSelection(formatted.length)
                isFormatting = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupKeyboardScrolling() {
        // Optional: handle scrolling to avoid keyboard overlap
    }
}
