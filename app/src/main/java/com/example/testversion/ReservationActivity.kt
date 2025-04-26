package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.FinalizedBooking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReservationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        val bookingsRecyclerView = findViewById<RecyclerView>(R.id.bookingsRecyclerView)
        bookingsRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val bookings = withContext(Dispatchers.IO) {
                val database = AppDatabase.getInstance(this@ReservationActivity)
                database.finalizedBookingDao().getAllBookingsOrdered()
            }

            val noBookingsTextView = findViewById<TextView>(R.id.noBookingsTextView)

            if (bookings.isEmpty()) {
                noBookingsTextView.visibility = View.VISIBLE
            } else {
                noBookingsTextView.visibility = View.GONE
            }

            bookingsRecyclerView.adapter = BookingAdapter(bookings) { bookingId ->
                val intent = Intent(this@ReservationActivity, BookingDetailsActivity::class.java)
                intent.putExtra("bookingId", bookingId)
                startActivity(intent)
            }
        }
    }
}