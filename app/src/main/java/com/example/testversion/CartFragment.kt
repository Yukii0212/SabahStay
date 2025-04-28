package com.example.testversion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment(), OnQuantityChangeListener {

    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<FoodOrder>()
    private val foodMap = mutableMapOf<Int, Food>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartAdapter = CartAdapter(cartItems, foodMap, this)
        view.findViewById<RecyclerView>(R.id.cartRecyclerView).adapter = cartAdapter

        // Load cart items
        lifecycleScope.launch {
            loadCartData()
        }
    }

    private suspend fun getCurrentCartId(): Int {
        val dao = AppDatabase.getInstance(requireContext()).foodDao()
        val existingCart = dao.getCartById(1)
        return existingCart?.id ?: createNewCart()
    }

    private suspend fun createNewCart(): Int {
        val dao = AppDatabase.getInstance(requireContext()).foodDao()
        val newCartId = dao.createCart(FoodCart())
        return newCartId.toInt()
    }

    private suspend fun loadCartData() {
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(requireContext()).foodDao()
            val cartId = getCurrentCartId()
            val orders = dao.getOrdersByCartId(cartId)
            val foods = dao.getAllFoodItems()

            cartItems.clear()
            cartItems.addAll(orders)

            foodMap.clear()
            foods.forEach { food -> foodMap[food.id] = food }
        }

        cartAdapter.notifyDataSetChanged()
    }

    override fun onQuantityChanged(position: Int, newQuantity: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val order = cartItems[position]
            val updatedOrder = order.copy(quantityOrdered = newQuantity)
            AppDatabase.getInstance(requireContext()).foodDao().insertFoodOrder(updatedOrder)

            withContext(Dispatchers.Main) {
                loadCartData()
            }
        }
    }
}