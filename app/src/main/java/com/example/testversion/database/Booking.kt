package com.example.testversion.database

import androidx.room.*
import org.threeten.bp.LocalDate

@Entity(tableName = "branches")
@TypeConverters(Converters::class)
data class Branch(
    @PrimaryKey val branchId: String,
    val name: String,
    val location: String,
    val contactNumber: String,
    val email: String,
    val imagePath: String = "",
    val description: String = ""
)

@Entity(
    tableName = "rooms",
    foreignKeys = [
        ForeignKey(
            entity = Branch::class,
            parentColumns = ["branchId"],
            childColumns = ["branchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("branchId")]
)
@TypeConverters(Converters::class)
data class HotelRoom(
    @PrimaryKey val roomId: String,
    val roomNumber: String,
    val roomType: String,
    val pricePerNight: Double,
    val isAvailable: Boolean,
    val bedCount: Int,
    val description: String,
    val maxGuests: Int,
    val imagePath: String = "",
    val floorNumber: Int = 0,
    val branchId: String
)

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HotelRoom::class,
            parentColumns = ["roomId"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userEmail"), Index("roomId")]
)
@TypeConverters(Converters::class)
data class Booking(
    @PrimaryKey val bookingId: String,
    val userEmail: String,
    val roomId: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val totalPrice: Double,
    val status: String,
    val numGuests: Int,
    val numberOfAdults: Int,
    val numberOfChildren: Int,
    val specialRequest: String = "",
    val createdAt: LocalDate,
    val paymentStatus: String
)

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = Booking::class,
            parentColumns = ["bookingId"],
            childColumns = ["bookingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HotelRoom::class,
            parentColumns = ["roomId"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookingId"), Index("userEmail"), Index("roomId")]
)
@TypeConverters(Converters::class)
data class Review(
    @PrimaryKey val reviewId: String,
    val bookingId: String,
    val userEmail: String,
    val roomId: String,
    val rating: Int,
    val reviewText: String = "",
    val createdAt: LocalDate
)