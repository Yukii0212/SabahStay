package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MountainRoom : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_island_room)

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // SINGLE ROOM SETUP
        setupRoomCard(
            R.id.single_room_card,
            R.drawable.mountain_single,
            "Single Room",
            "RM220 /per night",
            "1",
            MountainSingle::class.java
        )

        // DOUBLE ROOM SETUP
        setupRoomCard(
            R.id.double_room_card,
            R.drawable.mountain_double,
            "Double Room",
            "RM260 /per night",
            "2",
            MountainDouble::class.java
        )

        // QUEEN ROOM SETUP
        setupRoomCard(
            R.id.queen_room_card,
            R.drawable.mountain_queen,
            "Queen Room",
            "RM370 /per night",
            "2",
            MountainQueen::class.java
        )

        // DELUXE SUITE SETUP
        setupRoomCard(
            R.id.deluxe_suite_card,
            R.drawable.mountain_deluxe,
            "Deluxe Suite",
            "RM580 /per night",
            "3",
            MountainDeluxe::class.java
        )
    }

    private fun setupRoomCard(
        cardId: Int,
        imageResId: Int,
        title: String,
        price: String,
        personNumber: String,
        detailActivity: Class<*>
    ) {
        val cardView = findViewById<CardView>(cardId)
        val roomImage = cardView.findViewById<ImageView>(R.id.room_image)
        val roomTitle = cardView.findViewById<TextView>(R.id.room_title)
        val roomPrice = cardView.findViewById<TextView>(R.id.room_price)
        val personNum = cardView.findViewById<TextView>(R.id.person_number)
        val roomDetailsRow = cardView.findViewById<LinearLayout>(R.id.room_details_row)
        val bookNowButton = cardView.findViewById<Button>(R.id.bookingButton)

        roomImage.setImageResource(imageResId)
        roomTitle.text = title
        roomPrice.text = price
        personNum.text = personNumber

        roomDetailsRow.setOnClickListener {
            startActivity(Intent(this, detailActivity))
        }

        bookNowButton.setOnClickListener {
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
