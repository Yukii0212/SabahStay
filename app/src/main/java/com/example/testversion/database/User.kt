package com.example.testversion.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    val name: String,
    val passport: String,
    val gender: String,
    val phone: String,
    @PrimaryKey val email: String,
    val password: String
)