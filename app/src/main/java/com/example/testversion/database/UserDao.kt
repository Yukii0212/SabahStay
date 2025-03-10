package com.example.testversion.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users WHERE email = :email OR phone = :phone LIMIT 1")
    suspend fun getUserByEmailOrPhone(email: String, phone: String): User?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) OR phone = :phone OR passport = :passport LIMIT 1")
    suspend fun getUserByEmailOrPhoneOrPassport(email: String, phone: String, passport: String): User?
}

