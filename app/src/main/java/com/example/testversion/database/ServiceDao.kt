package com.example.testversion.database

import androidx.room.*

@Dao
interface ServiceDao {

    // Service operations
    @Insert
    suspend fun insertService(service: Service)

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: Int): Service?

    // Service usage operations
    @Insert
    suspend fun insertServiceUsage(serviceUsage: ServiceUsage)

    @Query("UPDATE service_usage SET cleaningRequestCount = :count WHERE bookingId = :bookingId")
    suspend fun updateCleaningRequestCount(bookingId: Int, count: Int)

    // Fetch all service usages for a specific bookingId
    @Query("SELECT * FROM service_usage WHERE bookingId = :bookingId")
    suspend fun getServiceUsageByBookingId(bookingId: String): List<ServiceUsage>

    @Query("SELECT cleaningRequestCount FROM service_usage WHERE bookingId = :bookingId ORDER BY id DESC LIMIT 1")
    suspend fun getCleaningRequestCount(bookingId: Int): Int?
}