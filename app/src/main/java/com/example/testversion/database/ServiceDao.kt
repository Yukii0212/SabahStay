package com.example.testversion.database

import androidx.room.*

@Dao
interface ServiceDao {

    // Service operations
    @Insert
    suspend fun insertService(service: Service)

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: Int): Service?

    @Query("UPDATE services SET price = :newPrice WHERE id = :serviceId")
    suspend fun updateServicePrice(serviceId: Int, newPrice: Double)

    // Service usage operations
    @Insert
    suspend fun insertServiceUsage(serviceUsage: ServiceUsage)

    @Query("SELECT * FROM service_usage WHERE bookingId = :bookingId")
    suspend fun getServiceUsageByBooking(bookingId: String): List<ServiceUsage>

    @Query("SELECT * FROM service_usage WHERE bookingId = :bookingId AND serviceId = :serviceId")
    suspend fun getServiceUsageForService(bookingId: String, serviceId: Int): List<ServiceUsage>

    @Query("UPDATE service_usage SET cleaningRequestCount = :count WHERE bookingId = :bookingId")
    suspend fun updateCleaningRequestCount(bookingId: Int, count: Int)

    @Update
    suspend fun updateServiceUsage(serviceUsage: ServiceUsage)

    @Query("SELECT cleaningRequestCount FROM service_usage WHERE bookingId = :bookingId ORDER BY id DESC LIMIT 1")
    suspend fun getCleaningRequestCount(bookingId: Int): Int?
}