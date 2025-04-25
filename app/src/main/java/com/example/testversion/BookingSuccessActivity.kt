package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView


class BookingSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_success)

        val bookingNumber = intent.getLongExtra("bookingNumber", -1L)
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)

        findViewById<TextView>(R.id.bookingNumberTextView).text = "Booking No: $bookingNumber"
        findViewById<TextView>(R.id.totalPriceTextView).text = "Total: RM %.2f".format(totalPrice)

        findViewById<Button>(R.id.homeButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

