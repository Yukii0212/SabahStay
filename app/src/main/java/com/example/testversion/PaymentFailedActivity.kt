package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PaymentFailedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_failed)

        val retryButton: Button = findViewById(R.id.retryButton)
        retryButton.setOnClickListener {
            val intent = Intent(this, PaymentDetailsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
