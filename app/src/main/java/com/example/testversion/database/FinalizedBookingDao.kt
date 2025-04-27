package com.example.testversion.database

import androidx.room.*
import org.threeten.bp.LocalDate

@Dao
@TypeConverters(Converters::class)
interface FinalizedBookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: FinalizedBooking)

    @Query("SELECT * FROM finalized_bookings WHERE bookingNumber = :bookingNumber")
    suspend fun getByBookingNumber(bookingNumber: Long): FinalizedBooking?

    @Query("SELECT MAX(bookingNumber) FROM finalized_bookings WHERE bookingNumber BETWEEN :start AND :end")
    suspend fun getMaxBookingNumberInRange(start: Long, end: Long): Long?

    @Query("SELECT * FROM finalized_bookings WHERE userEmail = :email")
    suspend fun getBookingsByEmail(email: String): List<FinalizedBooking>

    @Query("SELECT bookingNumber FROM finalized_bookings WHERE userEmail = :email ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestBookingIdForUser(email: String): Int?

    @Query("SELECT branchName FROM finalized_bookings WHERE bookingNumber = :bookingNumber")
    suspend fun getBranchByBookingNumber(bookingNumber: Int): String?

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