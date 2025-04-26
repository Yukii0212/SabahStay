package com.example.testversion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.R
import com.example.testversion.database.FinalizedBooking
import org.threeten.bp.format.DateTimeFormatter

class FinalizedBookingAdapter(private val bookings: List<FinalizedBooking>) :
    RecyclerView.Adapter<FinalizedBookingAdapter.BookingViewHolder>() {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookingNumber: TextView = view.findViewById(R.id.bookingNumber)
        val roomType: TextView = view.findViewById(R.id.roomType)
        val checkInDate: TextView = view.findViewById(R.id.checkInDate)
        val checkOutDate: TextView = view.findViewById(R.id.checkOutDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.bookingNumber.text = "Booking #: ${booking.bookingNumber}"
        holder.roomType.text = "Room: ${booking.roomType}"
        holder.checkInDate.text = "Check-in: ${booking.checkInDate.format(dateFormatter)}"
        holder.checkOutDate.text = "Check-out: ${booking.checkOutDate.format(dateFormatter)}"
    }

    override fun getItemCount(): Int = bookings.size
}