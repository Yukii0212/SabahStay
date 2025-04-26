package com.example.testversion.database

import androidx.room.*
import org.threeten.bp.LocalDate

@Entity(
    tableName = "finalized_bookings",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["email"], childColumns = ["userEmail"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = HotelRoom::class, parentColumns = ["roomId"], childColumns = ["roomId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("userEmail"), Index("roomId")]
)
data class FinalizedBooking(
    @PrimaryKey val bookingNumber: Long,
    val userEmail: String,
    val roomId: String,
    val roomType: String,
    val branchName: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val nights: Int,
    val basePrice: Double,
    val extraBed: Boolean,
    val lunchBuffetAdult: Int,
    val lunchBuffetChild: Int,
    val tax: Double,
    val totalPrice: Double,
    val paymentMethod: String,
    val createdAt: LocalDate,
    val numberOfAdults: Int,
    val numberOfChildren: Int
)
