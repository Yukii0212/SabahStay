package com.example.testversion.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser == null) {
                userDao.insert(user)
                true
            } else {
                false
            }
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }
}
