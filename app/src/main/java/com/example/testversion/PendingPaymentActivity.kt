package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PendingPaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_payment)

        val bookingNumber = intent.getLongExtra("bookingNumber", -1L)
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        val userName = intent.getStringExtra("userName") ?: ""
        val userPassport = intent.getStringExtra("userPassport") ?: ""
        val userPhone = intent.getStringExtra("userPhone") ?: ""
        val userEmail = intent.getStringExtra("userEmail") ?: ""
        val roomType = intent.getStringExtra("roomType") ?: ""
        val branchName = intent.getStringExtra("branchName") ?: ""

        val loadingSpinner = findViewById<ProgressBar>(R.id.loadingSpinner)
        val centerHourglass = findViewById<ImageView>(R.id.centerHourglass)
        val rightIcon = findViewById<ImageView>(R.id.rightIcon)
        val stepPayment = findViewById<TextView>(R.id.stepPayment)
        val stepConfirmation = findViewById<TextView>(R.id.stepConfirmation)

        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_spinner)
        loadingSpinner.startAnimation(rotateAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            centerHourglass.setImageResource(R.drawable.ic_checking)
            stepPayment.setTextColor(getColor(android.R.color.holo_green_dark))
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            rightIcon.setImageResource(R.drawable.ic_checking)
            stepConfirmation.setTextColor(getColor(android.R.color.holo_green_dark))

            val intent = Intent(this, BookingSuccessActivity::class.java)
            intent.putExtra("bookingNumber", bookingNumber)
            intent.putExtra("totalPrice", totalPrice)
            intent.putExtra("userName", userName)
            intent.putExtra("userPassport", userPassport)
            intent.putExtra("userPhone", userPhone)
            intent.putExtra("userEmail", userEmail)
            intent.putExtra("roomType", roomType)
            intent.putExtra("branchName", branchName)

            startActivity(intent)
            finish()
        }, 2000)
    }
}
