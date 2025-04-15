package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BookingSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_success)

        findViewById<Button>(R.id.homeButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Change to your main activity
            startActivity(intent)
            finish()
        }
    }
}
