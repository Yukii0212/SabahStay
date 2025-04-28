package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.Booking
import com.example.testversion.database.Branch
import com.example.testversion.database.HotelRoom
import com.example.testversion.database.Service
import com.example.testversion.database.User
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class OpenAnimation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_animation)


        val cloudMoveLeft = AnimationUtils.loadAnimation(this, R.anim.cloud_move_left)
        val cloudMoveRight = AnimationUtils.loadAnimation(this, R.anim.cloud_move_right)
        val mountainZoom = AnimationUtils.loadAnimation(this, R.anim.mountain_zoom)
        val textFadeIn = AnimationUtils.loadAnimation(this, R.anim.text_fade_in)


        val cloud1: ImageView = findViewById(R.id.cloud1)
        val cloud2: ImageView = findViewById(R.id.cloud2)
        val cloud3: ImageView = findViewById(R.id.cloud3)
        val cloud4: ImageView = findViewById(R.id.cloud4)
        val cloud5: ImageView = findViewById(R.id.cloud5)
        val cloud6: ImageView = findViewById(R.id.cloud6)
        val cloud7: ImageView = findViewById(R.id.cloud7)
        val cloud8: ImageView = findViewById(R.id.cloud8)

        val mountainView: ImageView = findViewById(R.id.mountainImageView)
        val titleText: TextView = findViewById(R.id.titleText)
        val subtitleText: TextView = findViewById(R.id.subtitleText)

        mountainView.visibility = View.VISIBLE

        // Start the cloud animations
        cloud1.startAnimation(cloudMoveLeft)
        cloud2.startAnimation(cloudMoveRight)
        cloud3.startAnimation(cloudMoveLeft)
        cloud4.startAnimation(cloudMoveRight)
        cloud5.startAnimation(cloudMoveLeft)
        cloud6.startAnimation(cloudMoveRight)
        cloud7.startAnimation(cloudMoveLeft)
        cloud8.startAnimation(cloudMoveRight)

        // Ensure clouds fully disperse
        val cloudAnimationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                cloud1.visibility = View.GONE
                cloud2.visibility = View.GONE
                cloud3.visibility = View.GONE
                cloud4.visibility = View.GONE
                cloud5.visibility = View.GONE
                cloud6.visibility = View.GONE
                cloud7.visibility = View.GONE
                cloud8.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        }

        cloudMoveLeft.setAnimationListener(cloudAnimationListener)
        cloudMoveRight.setAnimationListener(cloudAnimationListener)

        //  mountain zoom in animation
        mountainView.startAnimation(mountainZoom)

        // Make text **visible** at the right time
        Handler(Looper.getMainLooper()).postDelayed({
            titleText.visibility = View.VISIBLE
            subtitleText.visibility = View.VISIBLE
            titleText.startAnimation(textFadeIn)
            subtitleText.startAnimation(textFadeIn)
        }, (mountainZoom.duration * 0.6 - 1100).toLong())

        // Transition to next activity after the animation
        mountainZoom.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                Handler(Looper.getMainLooper()).postDelayed({

                    val intent = Intent(this@OpenAnimation, BranchOverview::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)


            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        insertRoomData()
    }

    private fun insertRoomData() {
        lifecycleScope.launch {
            val database = AppDatabase.getInstance(this@OpenAnimation)
            val serviceDao = database.serviceDao()
            val roomDao = database.roomDao()
            val branchDao = database.branchDao()

            //Insert the three branches
            val existingBranches = branchDao.getAllBranches()
            if (existingBranches.isEmpty()) {
                branchDao.insert(
                    Branch(
                        branchId = "kkcity",
                        name = "KK City",
                        location = "Kota Kinabalu",
                        contactNumber = "088-7019280",
                        email = "kkcs@sabahstay.com"
                    )
                )

                branchDao.insert(
                    Branch(
                        branchId = "kundasang",
                        name = "Kundasang",
                        location = "Kundasang",
                        contactNumber = "088-7019880",
                        email = "kundasang@sabahstay.com"
                    )
                )

                branchDao.insert(
                    Branch(
                        branchId = "island",
                        name = "Island Branch",
                        location = "Island Resort",
                        contactNumber = "088-7019680",
                        email = "island@sabahstay.com"
                    )
                )
            }

            val existingRooms = roomDao.getAll()
            if (existingRooms.isEmpty()) {
                //Insert all the rooms
                val roomsToInsert = listOf(
                    // KK City Branch
                    HotelRoom(
                        roomId = "KK-SGL-101",
                        roomNumber = "101",
                        roomType = "Single Room",
                        pricePerNight = 180.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in KK City - Business + City vibes",
                        maxGuests = 1,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-SGL-102",
                        roomNumber = "102",
                        roomType = "Single Room",
                        pricePerNight = 180.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in KK City - Business + City vibes",
                        maxGuests = 1,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-SGL-103",
                        roomNumber = "103",
                        roomType = "Single Room",
                        pricePerNight = 180.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in KK City - Business + City vibes",
                        maxGuests = 1,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-SGL-104",
                        roomNumber = "104",
                        roomType = "Single Room",
                        pricePerNight = 180.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in KK City - Business + City vibes",
                        maxGuests = 1,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-SGL-105",
                        roomNumber = "105",
                        roomType = "Single Room",
                        pricePerNight = 180.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in KK City - Business + City vibes",
                        maxGuests = 1,
                        branchId = "kkcity"
                    ),

                    HotelRoom(
                        roomId = "KK-DBL-101",
                        roomNumber = "101",
                        roomType = "Double Room",
                        pricePerNight = 240.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-DBL-102",
                        roomNumber = "102",
                        roomType = "Double Room",
                        pricePerNight = 240.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-DBL-103",
                        roomNumber = "103",
                        roomType = "Double Room",
                        pricePerNight = 240.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-DBL-104",
                        roomNumber = "104",
                        roomType = "Double Room",
                        pricePerNight = 240.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-DBL-105",
                        roomNumber = "105",
                        roomType = "Double Room",
                        pricePerNight = 240.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ),

                    HotelRoom(
                        roomId = "KK-QUEEN-101",
                        roomNumber = "101",
                        roomType = "Queen Room",
                        pricePerNight = 350.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ), HotelRoom(
                        roomId = "KK-QUEEN-102",
                        roomNumber = "102",
                        roomType = "Queen Room",
                        pricePerNight = 350.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ), HotelRoom(
                        roomId = "KK-QUEEN-103",
                        roomNumber = "103",
                        roomType = "Queen Room",
                        pricePerNight = 350.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-QUEEN-104",
                        roomNumber = "104",
                        roomType = "Queen Room",
                        pricePerNight = 350.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-QUEEN-105",
                        roomNumber = "105",
                        roomType = "Queen Room",
                        pricePerNight = 350.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in KK City - Business + City vibes",
                        maxGuests = 2,
                        branchId = "kkcity"
                    ),

                    HotelRoom(
                        roomId = "KK-DELUXE-101",
                        roomNumber = "101",
                        roomType = "Deluxe Suite",
                        pricePerNight = 550.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Deluxe Suite in KK City - Business + City vibes",
                        maxGuests = 3,
                        branchId = "kkcity"
                    ), HotelRoom(
                        roomId = "KK-DELUXE-102",
                        roomNumber = "102",
                        roomType = "Deluxe Suite",
                        pricePerNight = 550.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Deluxe Suite in KK City - Business + City vibes",
                        maxGuests = 3,
                        branchId = "kkcity"
                    ), HotelRoom(
                        roomId = "KK-DELUXE-103",
                        roomNumber = "103",
                        roomType = "Deluxe Suite",
                        pricePerNight = 550.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Deluxe Suite in KK City - Business + City vibes",
                        maxGuests = 3,
                        branchId = "kkcity"
                    ), HotelRoom(
                        roomId = "KK-DELUXE-104",
                        roomNumber = "104",
                        roomType = "Deluxe Suite",
                        pricePerNight = 550.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Deluxe Suite in KK City - Business + City vibes",
                        maxGuests = 3,
                        branchId = "kkcity"
                    ),
                    HotelRoom(
                        roomId = "KK-DELUXE-105",
                        roomNumber = "105",
                        roomType = "Deluxe Suite",
                        pricePerNight = 550.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Deluxe Suite in KK City - Business + City vibes",
                        maxGuests = 3,
                        branchId = "kkcity"
                    ),

                    // Kundasang Branch
                    HotelRoom(
                        roomId = "KDG-SGL-201",
                        roomNumber = "201",
                        roomType = "Single Room",
                        pricePerNight = 220.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Kundasang - Chill nature escape",
                        maxGuests = 1,
                        branchId = "kundasang"
                    ), HotelRoom(
                        roomId = "KDG-SGL-202",
                        roomNumber = "202",
                        roomType = "Single Room",
                        pricePerNight = 220.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Kundasang - Chill nature escape",
                        maxGuests = 1,
                        branchId = "kundasang"
                    ), HotelRoom(
                        roomId = "KDG-SGL-203",
                        roomNumber = "203",
                        roomType = "Single Room",
                        pricePerNight = 220.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Kundasang - Chill nature escape",
                        maxGuests = 1,
                        branchId = "kundasang"
                    ), HotelRoom(
                        roomId = "KDG-SGL-204",
                        roomNumber = "204",
                        roomType = "Single Room",
                        pricePerNight = 220.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Kundasang - Chill nature escape",
                        maxGuests = 1,
                        branchId = "kundasang"
                    ),
                    HotelRoom(
                        roomId = "KDG-SGL-205",
                        roomNumber = "205",
                        roomType = "Single Room",
                        pricePerNight = 220.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Kundasang - Chill nature escape",
                        maxGuests = 1,
                        branchId = "kundasang"
                    ),

                    HotelRoom(
                        roomId = "KDG-DBL-201",
                        roomNumber = "201",
                        roomType = "Double Room",
                        pricePerNight = 260.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ), HotelRoom(
                        roomId = "KDG-DBL-202",
                        roomNumber = "202",
                        roomType = "Double Room",
                        pricePerNight = 260.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ), HotelRoom(
                        roomId = "KDG-DBL-203",
                        roomNumber = "203",
                        roomType = "Double Room",
                        pricePerNight = 260.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ), HotelRoom(
                        roomId = "KDG-DBL-204",
                        roomNumber = "204",
                        roomType = "Double Room",
                        pricePerNight = 260.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ),
                    HotelRoom(
                        roomId = "KDG-DBL-205",
                        roomNumber = "205",
                        roomType = "Double Room",
                        pricePerNight = 260.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ),

                    HotelRoom(
                        roomId = "KDG-QUEEN-201",
                        roomNumber = "201",
                        roomType = "Queen Room",
                        pricePerNight = 370.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ), HotelRoom(
                        roomId = "KDG-QUEEN-202",
                        roomNumber = "202",
                        roomType = "Queen Room",
                        pricePerNight = 370.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ), HotelRoom(
                        roomId = "KDG-QUEEN-203",
                        roomNumber = "203",
                        roomType = "Queen Room",
                        pricePerNight = 370.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ), HotelRoom(
                        roomId = "KDG-QUEEN-204",
                        roomNumber = "204",
                        roomType = "Queen Room",
                        pricePerNight = 370.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ),
                    HotelRoom(
                        roomId = "KDG-QUEEN-205",
                        roomNumber = "205",
                        roomType = "Queen Room",
                        pricePerNight = 370.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Kundasang - Chill nature escape",
                        maxGuests = 2,
                        branchId = "kundasang"
                    ),

                    HotelRoom(
                        roomId = "KDG-MNT-201",
                        roomNumber = "201",
                        roomType = "Mountain View Suite",
                        pricePerNight = 580.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Mountain View Suite in Kundasang - Chill nature escape",
                        maxGuests = 3,
                        branchId = "kundasang"
                    ),
                    HotelRoom(
                        roomId = "KDG-MNT-202",
                        roomNumber = "202",
                        roomType = "Mountain View Suite",
                        pricePerNight = 580.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Mountain View Suite in Kundasang - Chill nature escape",
                        maxGuests = 3,
                        branchId = "kundasang"
                    ),
                    HotelRoom(
                        roomId = "KDG-MNT-203",
                        roomNumber = "203",
                        roomType = "Mountain View Suite",
                        pricePerNight = 580.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Mountain View Suite in Kundasang - Chill nature escape",
                        maxGuests = 3,
                        branchId = "kundasang"
                    ),
                    HotelRoom(
                        roomId = "KDG-MNT-204",
                        roomNumber = "204",
                        roomType = "Mountain View Suite",
                        pricePerNight = 580.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Mountain View Suite in Kundasang - Chill nature escape",
                        maxGuests = 3,
                        branchId = "kundasang"
                    ),
                    HotelRoom(
                        roomId = "KDG-MNT-205",
                        roomNumber = "205",
                        roomType = "Mountain View Suite",
                        pricePerNight = 580.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Mountain View Suite in Kundasang - Chill nature escape",
                        maxGuests = 3,
                        branchId = "kundasang"
                    ),

                    // Island Branch
                    HotelRoom(
                        roomId = "ISL-SGL-301",
                        roomNumber = "301",
                        roomType = "Single Room",
                        pricePerNight = 250.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 1,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-SGL-302",
                        roomNumber = "302",
                        roomType = "Single Room",
                        pricePerNight = 250.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 1,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-SGL-303",
                        roomNumber = "303",
                        roomType = "Single Room",
                        pricePerNight = 250.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 1,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-SGL-304",
                        roomNumber = "304",
                        roomType = "Single Room",
                        pricePerNight = 250.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 1,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-SGL-305",
                        roomNumber = "305",
                        roomType = "Single Room",
                        pricePerNight = 250.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Single Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 1,
                        branchId = "island"
                    ),

                    HotelRoom(
                        roomId = "ISL-DBL-301",
                        roomNumber = "301",
                        roomType = "Double Room",
                        pricePerNight = 320.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-DBL-302",
                        roomNumber = "302",
                        roomType = "Double Room",
                        pricePerNight = 320.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-DBL-303",
                        roomNumber = "303",
                        roomType = "Double Room",
                        pricePerNight = 320.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-DBL-304",
                        roomNumber = "304",
                        roomType = "Double Room",
                        pricePerNight = 320.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-DBL-305",
                        roomNumber = "305",
                        roomType = "Double Room",
                        pricePerNight = 320.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Double Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),

                    HotelRoom(
                        roomId = "ISL-QUEEN-301",
                        roomNumber = "301",
                        roomType = "Queen Room",
                        pricePerNight = 400.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-QUEEN-302",
                        roomNumber = "302",
                        roomType = "Queen Room",
                        pricePerNight = 400.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-QUEEN-303",
                        roomNumber = "303",
                        roomType = "Queen Room",
                        pricePerNight = 400.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-QUEEN-304",
                        roomNumber = "304",
                        roomType = "Queen Room",
                        pricePerNight = 400.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-QUEEN-305",
                        roomNumber = "305",
                        roomType = "Queen Room",
                        pricePerNight = 400.0,
                        isAvailable = true,
                        bedCount = 1,
                        description = "Queen Room in Island Branch - Tropical vacay vibes",
                        maxGuests = 2,
                        branchId = "island"
                    ),

                    HotelRoom(
                        roomId = "ISL-BEACH-301",
                        roomNumber = "301",
                        roomType = "Beachfront Suite",
                        pricePerNight = 700.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Beachfront Suite in Island Branch - Tropical vacay vibes",
                        maxGuests = 3,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-BEACH-302",
                        roomNumber = "302",
                        roomType = "Beachfront Suite",
                        pricePerNight = 700.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Beachfront Suite in Island Branch - Tropical vacay vibes",
                        maxGuests = 3,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-BEACH-303",
                        roomNumber = "303",
                        roomType = "Beachfront Suite",
                        pricePerNight = 700.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Beachfront Suite in Island Branch - Tropical vacay vibes",
                        maxGuests = 3,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-BEACH-304",
                        roomNumber = "304",
                        roomType = "Beachfront Suite",
                        pricePerNight = 700.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Beachfront Suite in Island Branch - Tropical vacay vibes",
                        maxGuests = 3,
                        branchId = "island"
                    ),
                    HotelRoom(
                        roomId = "ISL-BEACH-305",
                        roomNumber = "305",
                        roomType = "Beachfront Suite",
                        pricePerNight = 700.0,
                        isAvailable = true,
                        bedCount = 2,
                        description = "Beachfront Suite in Island Branch - Tropical vacay vibes",
                        maxGuests = 3,
                        branchId = "island"
                    )
                )

                roomsToInsert.forEach { room ->
                    roomDao.insert(room)
                }
                val insertedRooms = roomDao.getAll()
                insertedRooms.forEach {
                    android.util.Log.d(
                        "ROOM_DATA",
                        "Room: ${it.roomId} | ${it.roomType} | ${it.pricePerNight} | ${it.branchId}"
                    )
                }
            }

            val existingServices = serviceDao.getAllServices()
            if (existingServices.isEmpty()) {
                //Insert all the services
                serviceDao.insertService(
                    Service(
                        id = 1,
                        name = "Room Cleaning Service",
                        description = "Free for the first request, RM15 for subsequent requests."
                    )
                )
                serviceDao.insertService(
                    Service(
                        id = 2,
                        name = "Food Drinks Service",
                        description = "Provides food and drinks. Prices depend on the menu."
                    )
                )
                serviceDao.insertService(
                    Service(
                        id = 3,
                        name = "Toiletries Refill",
                        description = "Refill toiletries for RM15 per request."
                    )
                )
                serviceDao.insertService(
                    Service(
                        id = 4,
                        name = "Taxi / Airport Shuttle",
                        description = "Shuttle: RM20 (City), RM50 (Mountain/Island). Taxi: RM50 (City), RM100 (Mountain/Island)."
                    )
                )
                serviceDao.insertService(
                    Service(
                        id = 5,
                        name = "Laundry Service",
                        description = "Laundry service for RM20 per request."
                    )
                )
            }

            lifecycleScope.launch {
                val database = AppDatabase.getInstance(this@OpenAnimation)
                val foodDao = database.foodDao()

                val existingFoodItems = foodDao.getAllFoodItems()
                if (existingFoodItems.isEmpty()) {
                    val foodItems = listOf(
                        Food(
                            category = 1,
                            name = "Grilled Chicken Caesar Salad",
                            description = "A classic Caesar salad with tender, grilled chicken breast, crisp Romaine lettuce, crunchy croutons, and creamy Caesar dressing topped with parmesan shavings.",
                            price = 28.90,
                            ingredientsUsed = "Romaine lettuce, grilled chicken, Caesar dressing, croutons, parmesan cheese, olive oil, garlic",
                            imageResId = R.drawable.grilled_chicken_caesar_salad
                        ),
                        Food(
                            category = 1,
                            name = "Truffle Mushroom Pasta",
                            description = "A luxurious pasta dish with a rich and earthy truffle mushroom sauce, served with perfectly al dente fettuccine, finished with a sprinkle of parmesan and a drizzle of truffle oil.",
                            price = 36.50,
                            ingredientsUsed = "Fettuccine pasta, wild mushrooms, truffle oil, garlic, cream, parmesan cheese, olive oil, fresh herbs.",
                            imageResId = R.drawable.truffle_mushroom_pasta
                        ),
                        Food(
                            category = 1,
                            name = "Wagyu Beef Burger",
                            description = "A premium Wagyu beef patty grilled to perfection, served on a toasted brioche bun with lettuce, tomato, pickles, and our special sauce. Accompanied by crispy, golden fries.",
                            price = 42.00,
                            ingredientsUsed = "Wagyu beef patty, brioche bun, lettuce, tomato, pickles, special sauce, fries, olive oil.",
                            imageResId = R.drawable.wagyu_beef_burger
                        ),
                        Food(
                            category = 1,
                            name = "Pan-Seared Salmon with Asparagus",
                            description = "Fresh Atlantic salmon fillet pan-seared to a crispy golden brown, served with tender asparagus and a light citrus butter sauce to enhance the flavors.",
                            price = 48.00,
                            ingredientsUsed = "Atlantic salmon fillet, asparagus, lemon butter sauce, olive oil, garlic, fresh herbs.",
                            imageResId = R.drawable.salmon_with_asparagus
                        ),
                        Food(
                            category = 1,
                            name = "Margherita Pizza (Wood-Fired)",
                            description = "A classic Neapolitan pizza with a crisp, wood-fired crust topped with tangy tomato sauce, fresh mozzarella, and fragrant basil leaves.",
                            price = 32.50,
                            ingredientsUsed = "Pizza dough, mozzarella cheese, tomatoes, basil, olive oil, salt.",
                            imageResId = R.drawable.margherita_pizza
                        ),
                        Food(
                            category = 1,
                            name = "Lamb Shank with Red Wine Sauce",
                            description = "Slow-braised lamb shank, tender and flavorful, served with a rich, velvety red wine sauce and paired with creamy mashed potatoes.",
                            price = 58.00,
                            ingredientsUsed = "Lamb shank, red wine, carrots, onions, garlic, rosemary, thyme, potatoes, butter.",
                            imageResId = R.drawable.lamb_shank_red_wine
                        ),
                        Food(
                            category = 1,
                            name = "Seafood Aglio Olio",
                            description = "A decadent pasta dish with a medley of succulent seafood, tossed in a savory garlic and chili-infused olive oil sauce, and topped with a touch of fresh parsley.",
                            price = 39.90,
                            ingredientsUsed = "Spaghetti, shrimp, squid, garlic, chili flakes, olive oil, parsley, lemon zest.",
                            imageResId = R.drawable.seafood_aglio_olio
                        ),
                        Food(
                            category = 1,
                            name = "Roasted Duck Breast with Orange Glaze",
                            description = "A crispy-skinned duck breast, roasted to perfection and glazed with a sweet and tangy orange sauce, served with sautéed greens and roasted vegetables.",
                            price = 52.00,
                            ingredientsUsed = "Duck breast, orange juice, honey, garlic, thyme, olive oil, roasted vegetables.",
                            imageResId = R.drawable.roasted_duck_breast
                        ),
                    )

                    val drinksItems = listOf(
                        Food(
                            category = 2,
                            name = "Fresh Orange Juice",
                            description = "A refreshing and naturally sweet juice made with freshly squeezed oranges, perfect to start your day or complement any meal.",
                            price = 10.00,
                            ingredientsUsed = "Fresh oranges, ice.",
                            imageResId = R.drawable.orange_juice
                        ),
                        Food(
                            category = 2,
                            name = "Iced Latte",
                            description = "A smooth and bold espresso mixed with chilled milk and served over ice, a perfectly balanced pick-me-up.",
                            price = 14.50,
                            ingredientsUsed = "Espresso, milk, ice, sugar (optional)",
                            imageResId = R.drawable.iced_latte
                        ),
                        Food(
                            category = 2,
                            name = "Sparkling Water",
                            description = "Crisp, refreshing sparkling water with a gentle effervescence that cleanses your palate and pairs beautifully with any meal.",
                            price = 8.00,
                            ingredientsUsed = "Carbonated water.",
                            imageResId = R.drawable.sparkling_water
                        ),
                        Food(
                            category = 2,
                            name = "Coke-Cola",
                            description = "A fizzy and sweet cola, a perfect classic beverage for any occasion.",
                            price = 5.00,
                            ingredientsUsed = "Carbonated water, sugar, caramel color, caffeine, flavorings.",
                            imageResId = R.drawable.cola
                        ),
                        Food(
                            category = 2,
                            name = "English Breakfast Tea",
                            description = "A robust and full-bodied black tea, served hot with a splash of milk or lemon, ideal for a comforting break.",
                            price = 8.50,
                            ingredientsUsed = "Black tea leaves, milk (optional), lemon (optional).",
                            imageResId = R.drawable.english_tea
                        ),
                        Food(
                            category = 2,
                            name = "Matcha Latte (Hot/Iced)",
                            description = "A creamy and smooth matcha latte made with premium green tea powder and steamed milk, served hot or iced.",
                            price = 15.00,
                            ingredientsUsed = "Matcha powder, milk, ice (for iced version), honey (optional).",
                            imageResId = R.drawable.matcha_latte
                        ),
                        Food(
                            category = 2,
                            name = "Mineral Water (Still)",
                            description = "Pure and refreshing still mineral water, offering a clean taste and a perfect way to hydrate.",
                            price = 4.50,
                            ingredientsUsed = "Natural spring water.",
                            imageResId = R.drawable.mineral_water
                        ),
                    )

                    val alcoholItems = listOf(
                        Food(
                            category = 3,
                            name = "Red Wine",
                            description = "A smooth and velvety red wine with notes of dark berries and a hint of oak, perfect to pair with any of our rich dishes.",
                            price = 28.00,
                            ingredientsUsed = "Red wine (grape variety will vary by selection).",
                            imageResId = R.drawable.red_wine
                        ),
                        Food(
                            category = 3,
                            name = "White Wine",
                            description = "A crisp and refreshing white wine with citrus and floral notes, offering a light and bright complement to seafood and lighter dishes.",
                            price = 28.00,
                            ingredientsUsed = "White wine (grape variety will vary by selection).",
                            imageResId = R.drawable.white_wine
                        ),
                        Food(
                            category = 3,
                            name = "Mojito",
                            description = "A refreshing cocktail made with white rum, fresh mint leaves, lime juice, and a touch of sugar, topped off with soda water.",
                            price = 32.00,
                            ingredientsUsed = "White rum, mint leaves, lime, sugar, soda water.",
                            imageResId = R.drawable.mojito
                        ),
                        Food(
                            category = 3,
                            name = "Margarita",
                            description = "A tangy and flavorful cocktail with tequila, triple sec, and lime juice, served with a salted rim for that perfect balance of sweet and sour.",
                            price = 35.00,
                            ingredientsUsed = "Tequila, triple sec, lime juice, salt.",
                            imageResId = R.drawable.margarita
                        ),
                        Food(
                            category = 3,
                            name = "Gin & Tonic",
                            description = "A refreshing and crisp cocktail made with premium gin, tonic water, and a squeeze of lime.",
                            price = 30.00,
                            ingredientsUsed = "Gin, tonic water, lime.",
                            imageResId = R.drawable.gin_and_tonic
                        ),
                        Food(
                            category = 3,
                            name = "Whiskey (Single Shot)",
                            description = "A rich and smoky whiskey served neat, allowing the bold flavors of the spirit to shine through.",
                            price = 40.00,
                            ingredientsUsed = "Whiskey (distilled spirit, variety may vary).",
                            imageResId = R.drawable.whiskey
                        ),
                        Food(
                            category = 3,
                            name = "Beer",
                            description = " A rich and smoky whiskey served neat, allowing the bold flavors of the spirit to shine through.",
                            price = 18.00,
                            ingredientsUsed = "Water, malt, hops, yeast.",
                            imageResId = R.drawable.beer
                        ),
                    )

                    val dessertItems = listOf(
                        Food(
                            category = 4,
                            name = "Classic Tiramisu",
                            description = "A rich and creamy Italian dessert made with layers of coffee-soaked ladyfingers, mascarpone cream, and a dusting of cocoa powder.",
                            price = 24.00,
                            ingredientsUsed = "Ladyfingers, mascarpone cheese, coffee, cocoa powder, eggs, sugar.",
                            imageResId = R.drawable.tiramisu
                        ),
                        Food(
                            category = 4,
                            name = "Molten Lava Cake",
                            description = "A decadent chocolate cake with a gooey molten center, served warm with a scoop of vanilla ice cream",
                            price = 26.00,
                            ingredientsUsed = "Dark chocolate, butter, eggs, sugar, flour, vanilla ice cream.",
                            imageResId = R.drawable.lava_cake
                        ),
                        Food(
                            category = 4,
                            name = "Fruit Platter",
                            description = "A refreshing and colorful assortment of seasonal fruits, perfectly cut and served as a light and healthy option to finish your meal.",
                            price = 22.00,
                            ingredientsUsed = "Mixed seasonal fruits (e.g., watermelon, pineapple, strawberries, grapes).",
                            imageResId = R.drawable.fruit_platter
                        )
                    )

                    (foodItems + drinksItems + alcoholItems + dessertItems).forEach { food ->
                        foodDao.insertFood(food)
                    }
                }
            }
        }
    }
}

