package com.example.testversion.database

import androidx.room.*

@Dao
interface ServiceDao {

    // Service operations
    @Insert
    suspend fun insertService(service: Service)

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: Int): Service?

    @Query("SELECT * FROM services")
    suspend fun getAllServices(): List<Service>

    // Service usage operations
    @Insert
    suspend fun insertServiceUsage(serviceUsage: ServiceUsage)

    @Query("UPDATE service_usage SET cleaningRequestCount = :count WHERE bookingId = :bookingId")
    suspend fun updateCleaningRequestCount(bookingId: Int, count: Int)

    @Query("SELECT * FROM service_usage WHERE bookingId = :bookingId")
    suspend fun getServiceUsageByBookingId(bookingId: String): List<ServiceUsage>

    @Query("SELECT cleaningRequestCount FROM service_usage WHERE bookingId = :bookingId ORDER BY id DESC LIMIT 1")
    suspend fun getCleaningRequestCount(bookingId: Int): Int?

    @Query("SELECT * FROM service_usage WHERE bookingId = :bookingId AND serviceid = 5")
    fun getLaundryServiceUsageByBookingId(bookingId: String): List<ServiceUsage>

    @Query("UPDATE service_usage SET isPaid = 1 WHERE isPaid = 0")
    suspend fun updateAllIsPaid()
}