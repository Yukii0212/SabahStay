package com.example.testversion.database

import androidx.room.*
import android.content.Context
import org.threeten.bp.LocalDate
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.testversion.database.BookingDatabase.Companion.INSTANCE
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
    entities = [Branch::class, HotelRoom::class, Booking::class, Review::class, User::class, FinalizedBooking::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(BookingDatabase.LocalDateConverter::class)
abstract class BookingDatabase : RoomDatabase() {
    abstract fun finalizedBookingDao(): FinalizedBookingDao

    companion object {
        @Volatile
        private var INSTANCE: BookingDatabase? = null

        fun getInstance(context: Context): BookingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookingDatabase::class.java,
                    "booking_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
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
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `finalized_bookings` (
                `bookingNumber` INTEGER NOT NULL PRIMARY KEY,
                `branchName` TEXT NOT NULL,
                `tax` REAL NOT NULL,
                `lunchBuffetAdult` INTEGER NOT NULL,
                `lunchBuffetChild` INTEGER NOT NULL,
                `extraBed` INTEGER NOT NULL,
                `nights` INTEGER NOT NULL,
                `paymentMethod` TEXT NOT NULL,
                `basePrice` REAL NOT NULL,
                `roomType` TEXT NOT NULL,
                `totalPrice` REAL NOT NULL,
                `userEmail` TEXT NOT NULL,
                `roomId` TEXT NOT NULL,
                `checkInDate` TEXT NOT NULL,
                `checkOutDate` TEXT NOT NULL,
                `createdAt` TEXT NOT NULL,
                FOREIGN KEY(`roomId`) REFERENCES `rooms`(`roomId`) ON DELETE CASCADE,
                FOREIGN KEY(`userEmail`) REFERENCES `users`(`email`) ON DELETE CASCADE
            )
            """
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_finalized_bookings_userEmail` ON `finalized_bookings`(`userEmail`)"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_finalized_bookings_roomId` ON `finalized_bookings`(`roomId`)"
        )
    }
}