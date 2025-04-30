package com.example.testversion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.FinalizedBooking

class BookingAdapter(
    private val bookings: List<FinalizedBooking>,
    private val onBookingClick: (Long) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.bind(booking)
        holder.itemView.setOnClickListener {
            onBookingClick(booking.bookingNumber)
        }
    }

    override fun getItemCount() = bookings.size

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookingIdText: TextView = itemView.findViewById(R.id.bookingIdText)
        private val branchNameText: TextView = itemView.findViewById(R.id.branchNameText)
        private val roomTypeText: TextView = itemView.findViewById(R.id.roomTypeText)
        private val checkInDateText: TextView = itemView.findViewById(R.id.checkInDateText)
        private val checkOutDateText: TextView = itemView.findViewById(R.id.checkOutDateText)
        private val bookerNameText: TextView = itemView.findViewById(R.id.bookerNameText)
        private val numAdultsChildrenText: TextView = itemView.findViewById(R.id.numAdultsChildrenText)

        fun bind(booking: FinalizedBooking) {
            bookingIdText.text = "Booking ID: ${booking.bookingNumber}"
            branchNameText.text = "Branch: ${booking.branchName}"
            roomTypeText.text = "Room Type: ${booking.roomType}"
            checkInDateText.text = "Check-In: ${booking.checkInDate}"
            checkOutDateText.text = "Check-Out: ${booking.checkOutDate}"
            bookerNameText.text = "Booker: ${booking.userEmail}"
            numAdultsChildrenText.text = "Adults: ${booking.numberOfAdults}, Children: ${booking.numberOfChildren}"
        }
    }
}