package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MountainSingle : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mountain_single)

        val backButton = findViewById<ImageView>(R.id.back_button)
        val roomImage = findViewById<ImageView>(R.id.room_image)

        // Back button click
        backButton.setOnClickListener {
            finish() // Go back to previous screen
        }

        // Room image click to open in full screen
        roomImage.setOnClickListener {
            val intent = Intent(this, FullscreenImageActivity::class.java)
            intent.putExtra("imageResId", R.drawable.mountain_single)
            startActivity(intent)
        }

        val bookingButton = findViewById<Button>(R.id.bookingButton)

        bookingButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", null)

            if (userEmail.isNullOrEmpty()) {
                Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, SearchAvailableRoomActivity::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)
        }
    }
}
