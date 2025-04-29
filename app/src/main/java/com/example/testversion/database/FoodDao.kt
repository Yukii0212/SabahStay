package com.example.testversion

import androidx.room.*

@Dao
interface FoodDao {

    // Food operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: Food)

    @Query("SELECT * FROM food WHERE category = :category")
    suspend fun getFoodByCategory(category: Int): List<Food>

    @Query("SELECT * FROM food")
    suspend fun getAllFoodItems(): List<Food>

    @Query("SELECT * FROM Food WHERE id = :foodId")
    fun getFoodById(foodId: Int): Food

    // FoodOrder operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodOrder(foodOrder: FoodOrder)

    @Query("SELECT * FROM food_order WHERE cartId = :cartId")
    suspend fun getOrdersByCartId(cartId: Int): List<FoodOrder>

    @Query("UPDATE food_order SET quantityOrdered = :quantity WHERE id = :orderId")
    suspend fun updateOrderQuantity(orderId: Int, quantity: Int)

    @Query("DELETE FROM food_order WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Int)

    @Delete
    suspend fun deleteFoodOrder(foodOrder: FoodOrder)

    // FoodCart operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createCart(foodCart: FoodCart): Long

    @Query("DELETE FROM food_cart WHERE id = :cartId")
    suspend fun clearCart(cartId: Int)

    @Query("SELECT * FROM food_cart WHERE id = :cartId")
    suspend fun getCartById(cartId: Int): FoodCart?
}