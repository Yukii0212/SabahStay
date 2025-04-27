package com.example.testversion.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class Service(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val price: Double
)

@Entity(tableName = "service_usage")
data class ServiceUsage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: String,
    val roomNumber: String,
    val serviceId: Int,
    val serviceName: String,
    val price: Double,
    val requestTime: String,
    val requestDay: String,
    val isCanceled: Boolean = false,
    var cleaningRequestCount: Int = 0
)