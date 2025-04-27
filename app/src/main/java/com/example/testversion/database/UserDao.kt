package com.example.testversion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT email FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserEmail(email: String): String?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("UPDATE users SET nickname = :nickname WHERE email = :email")
    suspend fun updateNickname(email: String, nickname: String)

    @Query("UPDATE users SET profilePicturePath = :path WHERE email = :email")
    suspend fun updateProfilePicture(email: String, path: String)

    @Query("UPDATE users SET prefix = :prefix WHERE email = :email")
    suspend fun updatePrefix(email: String, prefix: String)

    @Query("UPDATE users SET gender = :gender WHERE email = :email")
    suspend fun updateGender(email: String, gender: String)

    @Query("UPDATE users SET phone = :phone WHERE email = :email")
    suspend fun updatePhone(email: String, phone: String)

    @Delete
    suspend fun delete(user: User)
}


