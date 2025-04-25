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


        val loadingSpinner = findViewById<ProgressBar>(R.id.loadingSpinner)
        val leftIcon = findViewById<ImageView>(R.id.leftIcon)
        val centerHourglass = findViewById<ImageView>(R.id.centerHourglass)
        val rightIcon = findViewById<ImageView>(R.id.rightIcon)
        val stepPayment = findViewById<TextView>(R.id.stepPayment)
        val stepConfirmation = findViewById<TextView>(R.id.stepConfirmation)

        // Start the status bar animation
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_spinner)
        loadingSpinner.startAnimation(rotateAnimation)

        //  Mark the 'Payment' step as done
        Handler(Looper.getMainLooper()).postDelayed({
            centerHourglass.setImageResource(R.drawable.ic_checking) // Replace hourglass with checked icon
            stepPayment.setTextColor(getColor(android.R.color.holo_green_dark)) // Change the text color to green color
        }, 1000)

        //  Mark the 'Confirmation' as done and move to next page
        Handler(Looper.getMainLooper()).postDelayed({
            rightIcon.setImageResource(R.drawable.ic_checking) // Mark theConfirmation as checked
            stepConfirmation.setTextColor(getColor(android.R.color.holo_green_dark)) // Change the text color to green color

            val intent = Intent(this, BookingSuccessActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}
