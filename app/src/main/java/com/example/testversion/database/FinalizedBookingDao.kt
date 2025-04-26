package com.example.testversion.database

import androidx.room.*
import org.threeten.bp.LocalDate

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

    @Query("""
    SELECT * FROM finalized_bookings 
    WHERE userEmail = :email AND 
          checkInDate <= :currentDate AND 
          checkOutDate >= :currentDate
""")
    suspend fun getCurrentBookings(email: String, currentDate: LocalDate): List<FinalizedBooking>

    @Query("""
    SELECT * FROM finalized_bookings 
    WHERE userEmail = :email AND 
          checkOutDate < :currentDate
""")
    suspend fun getPastBookings(email: String, currentDate: LocalDate): List<FinalizedBooking>

    @Query("""
    SELECT * FROM finalized_bookings 
    WHERE roomId = :roomId AND 
    NOT (checkOutDate <= :checkInDate OR checkInDate >= :checkOutDate)
""")
    suspend fun getConflictingFinalizedBookings(
        roomId: String,
        checkInDate: LocalDate,
        checkOutDate: LocalDate
    ): List<FinalizedBooking>
}