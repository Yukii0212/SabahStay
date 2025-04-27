package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        // Redirect to Room Cleaning page
        findViewById<ImageView>(R.id.roomCleaning).setOnClickListener {
            navigateToPage(RoomCleaningActivity::class.java)
        }

        // Redirect to Food Ordering page
        findViewById<ImageView>(R.id.foodOrdering).setOnClickListener {
            navigateToPage(FoodOrderingActivity::class.java)
        }

        // Redirect to Towel Refill page
        findViewById<ImageView>(R.id.towelRefill).setOnClickListener {
            navigateToPage(TowelRefillActivity::class.java)
        }

        // Redirect to Wake-Up Call page
        findViewById<ImageView>(R.id.wakeUpCall).setOnClickListener {
            navigateToPage(WakeUpCallActivity::class.java)
        }

        // Redirect to Laundry Service page
        findViewById<ImageView>(R.id.laundryService).setOnClickListener {
            navigateToPage(LaundryServiceActivity::class.java)
        }

        // Redirect to Request Pillows page
        findViewById<ImageView>(R.id.requestPillows).setOnClickListener {
            navigateToPage(RequestPillowsActivity::class.java)
        }

        // Redirect to Do Not Disturb page
        findViewById<ImageView>(R.id.doNotDisturb).setOnClickListener {
            navigateToPage(DoNotDisturbActivity::class.java)
        }

        // Redirect to Chat with Reception page
        findViewById<ImageView>(R.id.chatReception).setOnClickListener {
            navigateToPage(ChatReceptionActivity::class.java)
        }

        // Redirect to Taxi Booking page
        findViewById<ImageView>(R.id.taxiBooking).setOnClickListener {
            navigateToPage(TaxiBookingActivity::class.java)
        }

        // Redirect to View Bills page
        findViewById<ImageView>(R.id.viewBills).setOnClickListener {
            navigateToPage(ViewBillsActivity::class.java)
        }

        // Redirect to Pay Bills page
        findViewById<ImageView>(R.id.payBills).setOnClickListener {
            navigateToPage(PayBillsActivity::class.java)
        }
    }

    private fun navigateToPage(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}