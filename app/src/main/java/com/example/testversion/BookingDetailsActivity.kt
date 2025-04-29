package com.example.testversion

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)

        val bookingId = intent.getLongExtra("bookingId", -1L)

        if (bookingId != -1L) {
            lifecycleScope.launch {
                val booking = withContext(Dispatchers.IO) {
                    val database = AppDatabase.getInstance(this@BookingDetailsActivity)
                    database.finalizedBookingDao().getByBookingNumber(bookingId)
                }

                val roomNumber = withContext(Dispatchers.IO) {
                    val database = AppDatabase.getInstance(this@BookingDetailsActivity)
                    booking?.let {
                        database.roomDao().getRoomNumberById(it.roomId)
                    }
                }

                booking?.let {
                    findViewById<TextView>(R.id.bookingIdText).text = "Booking ID: ${it.bookingNumber}"
                    findViewById<TextView>(R.id.branchNameText).text = "Branch: ${it.branchName}"
                    findViewById<TextView>(R.id.roomTypeText).text = "Room Type: ${it.roomType}"
                    findViewById<TextView>(R.id.roomNumberText).text = "Room Number: $roomNumber"
                    findViewById<TextView>(R.id.checkInDateText).text = "Check-In: ${it.checkInDate}"
                    findViewById<TextView>(R.id.checkOutDateText).text = "Check-Out: ${it.checkOutDate}"
                    findViewById<TextView>(R.id.bookerNameText).text = "Booker: ${it.userEmail}"
                    findViewById<TextView>(R.id.numAdultsChildrenText).text = "Adults: ${it.numberOfAdults}, Children: ${it.numberOfChildren}"
                    findViewById<TextView>(R.id.totalPriceText).text = "Total Price: RM${"%.2f".format(it.totalPrice)}"
                    findViewById<TextView>(R.id.paymentMethodText).text = "Payment Method: ${it.paymentMethod}"
                }
            }
        }
    }
}