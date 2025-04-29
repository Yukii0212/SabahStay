package com.example.testversion

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: Int, // 1 = Food, 2 = Drinks, 3 = Alcohol, 4 = Dessert
    val name: String,
    val description: String,
    val price: Double,
    val ingredientsUsed: String,
    val imageResId: Int
)

@Entity(tableName = "food_order")
data class FoodOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodId: Int,
    val cartId: Int,
    val quantityOrdered: Int
)

@Entity(tableName = "food_cart")
data class FoodCart(
    @PrimaryKey val id: Int = 1
)

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    var quantityOrdered: Int,
    val cartId: Int,
    val category: Int,
    val description: String,
    val ingredientsUsed: String,
    val imageResId: Int
)