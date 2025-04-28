package com.example.testversion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FoodFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_food, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val database = AppDatabase.getInstance(requireContext())
            val foodDao = database.foodDao()

            // Fetch all food items with category 1
            val foodList = withContext(Dispatchers.IO) {
                foodDao.getFoodByCategory(1)
            }

            // Set up the adapter
            val adapter = FoodAdapter(foodList) { food ->
                addToCart(food, foodDao)
            }
            recyclerView.adapter = adapter
        }

        return view
    }

    private fun addToCart(food: Food, foodDao: FoodDao) {
        lifecycleScope.launch(Dispatchers.IO) {
            val cartId = getOrCreateCartId(foodDao)
            Log.d("CartDebug", "Cart ID: $cartId")

            val existingOrder = foodDao.getOrdersByCartId(cartId).find { it.foodId == food.id }
            Log.d("CartDebug", "Existing Order: $existingOrder")

            if (existingOrder != null) {
                val updatedOrder = existingOrder.copy(quantityOrdered = existingOrder.quantityOrdered + 1)
                foodDao.insertFoodOrder(updatedOrder)
                Log.d("CartDebug", "Updated Order: $updatedOrder")
            } else {
                val newOrder = FoodOrder(foodId = food.id, cartId = cartId, quantityOrdered = 1)
                foodDao.insertFoodOrder(newOrder)
                Log.d("CartDebug", "New Order: $newOrder")
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "${food.name} added to cart",
                    Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.setFragmentResult("cartUpdated", Bundle())
            }
        }
    }

    private suspend fun getOrCreateCartId(foodDao: FoodDao): Int {
        val existingCart = foodDao.getCartById(1)
        return existingCart?.id ?: foodDao.createCart(FoodCart()).toInt()
    }
}