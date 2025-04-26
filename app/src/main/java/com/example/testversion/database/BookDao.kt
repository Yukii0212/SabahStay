package com.example.testversion.database

import androidx.room.*
import org.threeten.bp.LocalDate


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

    @Query("SELECT * FROM rooms WHERE branchId = :branchId AND roomType = :roomType LIMIT 1")
    suspend fun getByBranchAndRoomType(branchId: String, roomType: String): HotelRoom?

    @Query("SELECT * FROM rooms WHERE branchId = :branchId AND roomType = :roomType AND isAvailable = 1 LIMIT 1")
    suspend fun getAvailableByBranchAndRoomType(branchId: String, roomType: String): HotelRoom?

    @Query("""
    SELECT * FROM rooms 
    WHERE branchId = :branchId 
      AND roomType = :roomType 
      AND isAvailable = 1
      AND roomId NOT IN (
          SELECT roomId FROM bookings
          WHERE NOT (checkOutDate <= :checkIn OR checkInDate >= :checkOut)
      )
    LIMIT 1
""")
    suspend fun findAvailableRoomBetweenDates(
        branchId: String,
        roomType: String,
        checkIn: LocalDate,
        checkOut: LocalDate
    ): HotelRoom?

}


@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: Booking)

    @Query("""
    SELECT * FROM bookings 
    WHERE roomId = :roomId AND 
    NOT (checkOutDate <= :checkInDate OR checkInDate >= :checkOutDate)
""")
    suspend fun getConflictingBookings(
        roomId: String,
        checkInDate: LocalDate,
        checkOutDate: LocalDate
    ): List<Booking>
}

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: Review)
}
