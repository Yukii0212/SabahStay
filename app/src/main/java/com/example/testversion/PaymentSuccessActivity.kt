package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PaymentSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        val continueButton: Button = findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val intent = Intent(this, BranchOverview::class.java)
            startActivity(intent)
            finish()
        }
    }
}