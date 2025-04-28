import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.CartItem
import com.example.testversion.FoodCart
import com.example.testversion.FoodDao
import com.example.testversion.FoodOrder
import com.example.testversion.R
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val database = AppDatabase.getInstance(requireContext())
            val foodDao = database.foodDao()

            try {
                Log.d("CartDebug", "Starting to fetch cart data")

                val cartId = withContext(Dispatchers.IO) {
                    Log.d("CartDebug", "Fetching or creating cart ID")
                    getOrCreateCartId(foodDao).also {
                        Log.d("CartDebug", "Cart ID fetched/created: $it")
                    }
                }

                val foodOrders = withContext(Dispatchers.IO) {
                    Log.d("CartDebug", "Fetching food orders for cart ID: $cartId")
                    foodDao.getOrdersByCartId(cartId).also {
                        Log.d("CartDebug", "Fetched Food Orders: $it")
                    }
                }

                val cartItems = withContext(Dispatchers.IO) {
                    Log.d("CartDebug", "Mapping food orders to cart items")
                    foodOrders.map { foodOrder ->
                        val food = foodDao.getFoodById(foodOrder.foodId).also {
                            Log.d("CartDebug", "Fetched Food for FoodOrder ID ${foodOrder.id}: $it")
                        }
                        CartItem(
                            id = foodOrder.id,
                            name = food.name,
                            price = food.price,
                            quantityOrdered = foodOrder.quantityOrdered,
                            cartId = foodOrder.cartId,
                            category = food.category,
                            description = food.description,
                            ingredientsUsed = food.ingredientsUsed,
                            imageResId = food.imageResId
                        )
                    }.also {
                        Log.d("CartDebug", "Mapped Cart Items: $it")
                    }
                }

                Log.d("CartDebug", "Finished fetching and mapping cart data")

                recyclerView.adapter = CartAdapter(
                    cartItems,
                    onIncreaseQuantity = { _ ->  },
                    onDecreaseQuantity = { _ -> },
                    onRemoveItem = { _ ->  }
                )
            } catch (e: Exception) {
                Log.e("CartDebug", "Error fetching cart items", e)
                Toast.makeText(requireContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private suspend fun getOrCreateCartId(foodDao: FoodDao): Int {
        val existingCart = foodDao.getCartById(1)
        return existingCart?.id ?: foodDao.createCart(FoodCart()).toInt()
    }

    private fun refreshCart() {
        lifecycleScope.launch(Dispatchers.Main) {
            onCreateView(layoutInflater, null, null)
        }
    }
}