package com.example.testversion.database

import androidx.room.*
import android.content.Context
import org.threeten.bp.LocalDate
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import org.threeten.bp.format.DateTimeFormatter

@Entity(tableName = "branches")
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

data class Booking(
    @PrimaryKey val bookingId: String,
    val userEmail: String,
    val roomId: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val totalPrice: Double,
    val status: String,
    val numGuests: Int,
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

data class Review(
    @PrimaryKey val reviewId: String,
    val bookingId: String,
    val userEmail: String,
    val roomId: String,
    val rating: Int,
    val reviewText: String = "",
    val createdAt: LocalDate
)

@Database(
    entities = [Branch::class, HotelRoom::class, Booking::class, Review::class, User::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(LocalDateConverter::class)
    abstract class BookingDatabase : RoomDatabase() {
    abstract fun branchDao(): BranchDao
    abstract fun roomDao(): RoomDao
    abstract fun bookingDao(): BookingDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: BookingDatabase? = null

        fun getInstance(context: Context): BookingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookingDatabase::class.java,
                    "booking_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class LocalDateConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, formatter) }
    }
}