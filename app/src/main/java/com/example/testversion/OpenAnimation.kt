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

    private fun insertRoomData(){
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
        }
    }
}

