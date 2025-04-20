package com.example.testversion.database

import androidx.room.*

@Dao
interface FinalizedBookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: FinalizedBooking)

    @Query("SELECT * FROM finalized_bookings WHERE userEmail = :email")
    suspend fun getByUser(email: String): List<FinalizedBooking>

    @Query("SELECT * FROM finalized_bookings WHERE bookingNumber = :bookingNumber")
    suspend fun getByBookingNumber(bookingNumber: Long): FinalizedBooking?

    @Query("SELECT MAX(bookingNumber) FROM finalized_bookings WHERE bookingNumber BETWEEN :start AND :end")
    suspend fun getMaxBookingNumberInRange(start: Long, end: Long): Long?
}
