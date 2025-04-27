package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.ServiceDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PayBillsActivity : AppCompatActivity() {
    private lateinit var paymentOptionsLayout: ConstraintLayout
    private lateinit var creditCardForm: ConstraintLayout
    private lateinit var confirmButton: Button
    private lateinit var cardNumberEditText: EditText
    private lateinit var expiryDateEditText: EditText
    private var selectedPaymentMethod: String = ""
    private lateinit var serviceDao: ServiceDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_details)

        paymentOptionsLayout = findViewById(R.id.paymentOptionsLayout)
        creditCardForm = findViewById(R.id.creditCardForm)
        confirmButton = findViewById(R.id.confirmButton)
        cardNumberEditText = findViewById(R.id.cardNumber)
        expiryDateEditText = findViewById(R.id.expiryDate)

        val creditCardLayout = findViewById<LinearLayout>(R.id.creditCardLayout)
        val onlineBankingLayout = findViewById<LinearLayout>(R.id.onlineBankingLayout)
        val eWalletLayout = findViewById<LinearLayout>(R.id.eWalletLayout)
        val database = AppDatabase.getInstance(this)
        serviceDao = database.serviceDao()

        confirmButton.visibility = View.GONE

        creditCardLayout.setOnClickListener {
            selectedPaymentMethod = "credit_card"
            showCreditCardForm()
        }

        onlineBankingLayout.setOnClickListener {
            selectedPaymentMethod = "online_banking"
            confirmButton.visibility = View.VISIBLE
            confirmButton.setOnClickListener { processPayment() }
        }

        eWalletLayout.setOnClickListener {
            selectedPaymentMethod = "e_wallet"
            confirmButton.visibility = View.VISIBLE
            confirmButton.setOnClickListener { processPayment() }
        }

        confirmButton.setOnClickListener {
            if (selectedPaymentMethod == "credit_card") {
                validateAndProceed()
            } else {
                processPayment()
            }
        }

        setupCardNumberFormatter()
        setupExpiryDateFormatter()
    }

    private fun showCreditCardForm() {
        findViewById<LinearLayout>(R.id.creditCardLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.onlineBankingLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.eWalletLayout).visibility = View.GONE

        creditCardForm.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(500).setInterpolator(DecelerateInterpolator()).start()
        }

        confirmButton.visibility = View.VISIBLE
    }

    private fun validateAndProceed() {
        val cardNumber = cardNumberEditText.text.toString().replace(" ", "")
        val expiryDate = expiryDateEditText.text.toString()

        if (cardNumber.isBlank() || expiryDate.isBlank()) {
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
        }

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

    private fun processPayment() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                serviceDao.updateAllIsPaid()
            }

            runOnUiThread {
                Toast.makeText(this@PayBillsActivity, "Payment successful!", Toast.LENGTH_LONG).show()
                val intent = Intent(this@PayBillsActivity, PaymentSuccessActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupCardNumberFormatter() {
        cardNumberEditText.addTextChangedListener(object : android.text.TextWatcher {
            private var isFormatting: Boolean = false
            override fun afterTextChanged(s: android.text.Editable?) {
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
        expiryDateEditText.addTextChangedListener(object : android.text.TextWatcher {
            private var isFormatting = false
            override fun afterTextChanged(s: android.text.Editable?) {
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
}