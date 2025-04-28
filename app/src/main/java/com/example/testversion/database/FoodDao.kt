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

    @Query("DELETE FROM food")
    suspend fun deleteAllFood()

    // FoodOrder operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodOrder(foodOrder: FoodOrder)

    @Query("SELECT * FROM food_order WHERE cartId = :cartId")
    suspend fun getOrdersByCartId(cartId: Int): List<FoodOrder>

    @Delete
    suspend fun deleteFoodOrder(foodOrder: FoodOrder)

    // FoodCart operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createCart(foodCart: FoodCart): Long

    @Query("SELECT * FROM food")
    suspend fun getAllCartItems(): List<Food>

    @Query("DELETE FROM food_cart WHERE id = :cartId")
    suspend fun clearCart(cartId: Int)

    @Query("SELECT * FROM food_cart WHERE id = :cartId")
    suspend fun getCartById(cartId: Int): FoodCart?

    @Query("""
    SELECT f.*, fo.quantityOrdered, fo.cartId
    FROM food f
    INNER JOIN food_order fo ON f.id = fo.foodId
    WHERE fo.cartId = :cartId
""")
    suspend fun getCartItemsByCartId(cartId: Int): List<CartItem>
}