package com.example.testversion.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.testversion.Food
import com.example.testversion.FoodCart
import com.example.testversion.FoodDao
import com.example.testversion.FoodOrder

@Database(
    entities = [
        Service::class, ServiceUsage::class, User::class, Branch::class,
        HotelRoom::class, Booking::class, Review::class, FinalizedBooking::class,
        Food::class, FoodOrder::class, FoodCart::class
    ],
    version = 15,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun branchDao(): BranchDao
    abstract fun roomDao(): RoomDao
    abstract fun bookingDao(): BookingDao
    abstract fun reviewDao(): ReviewDao
    abstract fun finalizedBookingDao(): FinalizedBookingDao
    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sabahstay_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}


