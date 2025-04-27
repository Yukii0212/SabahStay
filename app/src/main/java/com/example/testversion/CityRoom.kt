package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class CityRoom : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_room)

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // SINGLE ROOM SETUP
        setupRoomCard(
            R.id.single_room_card,
            R.drawable.city_single,
            "Single Room",
            "RM180 /per night",
            "1",
            CitySingle::class.java
        )

        // DOUBLE ROOM SETUP
        setupRoomCard(
            R.id.double_room_card,
            R.drawable.city_double,
            "Double Room",
            "RM240 /per night",
            "2",
            CityDouble::class.java
        )

        // QUEEN ROOM SETUP
        setupRoomCard(
            R.id.queen_room_card,
            R.drawable.city_queen,
            "Queen Room",
            "RM350 /per night",
            "2",
            CityQueen::class.java
        )

        // DELUXE SUITE SETUP
        setupRoomCard(
            R.id.deluxe_suite_card,
            R.drawable.city_deluxe,
            "Deluxe Suite",
            "RM550 /per night",
            "3",
            CityDeluxe::class.java
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
        val bookNowButton = cardView.findViewById<Button>(R.id.book_now_button)

        roomImage.setImageResource(imageResId)
        roomTitle.text = title
        roomPrice.text = price
        personNum.text = personNumber

        roomDetailsRow.setOnClickListener {
            startActivity(Intent(this, detailActivity))
        }

        bookNowButton.setOnClickListener {
            Toast.makeText(this, "Booking $title...", Toast.LENGTH_SHORT).show()
            // Navigate to Booking Page if you have one
        }
    }
}
