// PaymentDetailsActivity.kt
package com.example.testversion

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Calendar


class PaymentDetailsActivity : AppCompatActivity() {
    private lateinit var paymentOptionsLayout: ConstraintLayout
    private lateinit var creditCardForm: ConstraintLayout
    private lateinit var confirmButton: Button
    private lateinit var cardNumberEditText: EditText
    private lateinit var expiryDateEditText: EditText
    private lateinit var centerHourglass: ImageView
    private lateinit var rightIcon: ImageView
    private var selectedPaymentMethod: String = ""

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

        confirmButton.visibility = View.GONE // Hide confirm initially

        creditCardLayout.setOnClickListener {
            selectedPaymentMethod = "credit_card"
            showCreditCardForm()
        }

        onlineBankingLayout.setOnClickListener {
            selectedPaymentMethod = "online_banking"
            confirmButton.visibility = View.VISIBLE
            confirmButton.setOnClickListener {
                processPayment()  // Only when user CONFIRMS
            }
        }

        eWalletLayout.setOnClickListener {
            selectedPaymentMethod = "e_wallet"
            confirmButton.visibility = View.VISIBLE
            confirmButton.setOnClickListener {
                processPayment()  // Only when user CONFIRMS
            }
        }

        confirmButton.setOnClickListener {
            if (selectedPaymentMethod == "credit_card") {
                validateAndProceed()   // validate expiry + card details
            } else {
                processPayment()       // online banking, e-wallet
            }
        }


        setupCardNumberFormatter()
        setupExpiryDateFormatter()
        setupKeyboardScrolling()
    }

    private fun showCreditCardForm() {
        findViewById<LinearLayout>(R.id.creditCardLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.onlineBankingLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.eWalletLayout).visibility = View.GONE

        val selectedPaymentMethodLayout = findViewById<LinearLayout>(R.id.selectedPaymentMethodLayout)
        selectedPaymentMethodLayout.visibility = View.VISIBLE

        val selectedPaymentText = findViewById<TextView>(R.id.selectedPaymentText)


        selectedPaymentText.text = "Credit Card"


        // Fade in the credit card form
        creditCardForm.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        confirmButton.visibility = View.VISIBLE // Show confirm button when needed
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
        processPayment()
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



    private fun animateProgressBar() {
        centerHourglass.setImageResource(R.drawable.ic_checking)
        rightIcon.setImageResource(R.drawable.ic_checking)

        val scaleUp = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f)
        val scaleYUp = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)

        ObjectAnimator.ofPropertyValuesHolder(centerHourglass, scaleUp, scaleYUp).apply {
            duration = 500
            interpolator = DecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofPropertyValuesHolder(rightIcon, scaleUp, scaleYUp).apply {
            duration = 500
            interpolator = DecelerateInterpolator()
            start()
        }

        // Animate the lines color
        val stepProgressBar = findViewById<LinearLayout>(R.id.stepProgressBar)
        val leftLine = stepProgressBar.getChildAt(0)
        val rightLine = stepProgressBar.getChildAt(2)

        ObjectAnimator.ofArgb(leftLine, "backgroundColor", 0xFFFFFFFF.toInt(), 0xFF4CAF50.toInt()).apply {
            duration = 500
            start()
        }

        ObjectAnimator.ofArgb(rightLine, "backgroundColor", 0xFFFFFFFF.toInt(), 0xFF4CAF50.toInt()).apply {
            duration = 500
            start()
        }
    }

    private fun processPayment() {
        confirmButton.text = "Processing..."
        confirmButton.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, PendingPaymentActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun setupCardNumberFormatter() {
        cardNumberEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true
                val digitsOnly = s.toString().replace("\\D".toRegex(), "")
                val formatted = StringBuilder()
                for (i in digitsOnly.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(digitsOnly[i])
                }
                cardNumberEditText.setText(formatted.toString())
                cardNumberEditText.setSelection(formatted.length)
                isFormatting = false
            }
        })
    }

    private fun setupExpiryDateFormatter() {
        expiryDateEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true
                val digitsOnly = s.toString().replace("\\D".toRegex(), "")
                val formatted = when {
                    digitsOnly.length >= 3 -> digitsOnly.substring(0, 2) + "/" + digitsOnly.substring(2)
                    else -> digitsOnly
                }
                expiryDateEditText.setText(formatted)
                expiryDateEditText.setSelection(formatted.length)
                isFormatting = false
            }
        })
    }

    private fun setupKeyboardScrolling() {
        val scrollView = findViewById<ScrollView>(R.id.scrollView)

        val cardHolderNameEditText = findViewById<EditText>(R.id.cardHolderName)
        val cvvEditText = findViewById<EditText>(R.id.cvv)

        val editTextList = listOf(cardNumberEditText, cardHolderNameEditText, expiryDateEditText, cvvEditText)

        editTextList.forEach { editText ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    scrollView.postDelayed({
                        scrollView.smoothScrollTo(0, editText.top - 100)
                    }, 200)
                }
            }
        }
    }

}
