package com.example.testversion.database

import androidx.room.*
import java.time.LocalDate

@Dao
interface BranchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(branch: Branch)

    @Query("SELECT * FROM branches")
    suspend fun getAllBranches(): List<Branch>

    @Query("SELECT * FROM branches")
    suspend fun getAll(): List<Branch>
}

@Dao
interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(room: HotelRoom)

    @Query("SELECT * FROM rooms")
    suspend fun getAll(): List<HotelRoom>

    @Query("SELECT * FROM rooms WHERE branchId = :branchId")
    suspend fun getByBranch(branchId: String): List<HotelRoom>
}


@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: Booking)

    @Query("SELECT * FROM bookings")
    suspend fun getAll(): List<Booking>

    @Query("SELECT * FROM bookings WHERE userEmail = :userEmail")
    suspend fun getByUser(userEmail: String): List<Booking>

    @Query("""
    SELECT * FROM bookings 
    WHERE roomId = :roomId AND 
    NOT (checkOutDate <= :checkInDate OR checkInDate >= :checkOutDate)
""")
    suspend fun getConflictingBookings(
        roomId: String,
        checkInDate: org.threeten.bp.LocalDate,
        checkOutDate: org.threeten.bp.LocalDate
    ): List<Booking>

    @Query("SELECT * FROM bookings WHERE roomId = :roomId")
    suspend fun getByRoom(roomId: String): List<Booking>

}

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: Review)

    @Query("SELECT * FROM reviews WHERE roomId = :roomId")
    suspend fun getByRoom(roomId: String): List<Review>

    @Query("SELECT * FROM reviews WHERE userEmail = :userEmail")
    suspend fun getByUser(userEmail: String): List<Review>
}
