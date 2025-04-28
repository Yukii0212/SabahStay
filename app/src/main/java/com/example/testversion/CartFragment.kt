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
                val cartId = withContext(Dispatchers.IO) {
                    getOrCreateCartId(foodDao)
                }

                val foodOrders = withContext(Dispatchers.IO) {
                    foodDao.getOrdersByCartId(cartId)
                }
                Log.d("CartDebug", "Fetched Food Orders: $foodOrders")

                val cartItems = withContext(Dispatchers.IO) {
                    foodOrders.map { foodOrder ->
                        val food = foodDao.getFoodById(foodOrder.foodId)
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
                    }
                }

                // Define callbacks and set adapter (unchanged)
                val onIncreaseQuantity: (CartItem) -> Unit = { cartItem ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val updatedOrder = FoodOrder(
                            id = cartItem.id,
                            foodId = cartItem.id,
                            cartId = cartItem.cartId,
                            quantityOrdered = cartItem.quantityOrdered + 1
                        )
                        foodDao.insertFoodOrder(updatedOrder)
                        refreshCart()
                    }
                }

                val onDecreaseQuantity: (CartItem) -> Unit = { cartItem ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (cartItem.quantityOrdered > 1) {
                            val updatedOrder = FoodOrder(
                                id = cartItem.id,
                                foodId = cartItem.id,
                                cartId = cartItem.cartId,
                                quantityOrdered = cartItem.quantityOrdered - 1
                            )
                            foodDao.insertFoodOrder(updatedOrder)
                        } else {
                            foodDao.deleteFoodOrder(FoodOrder(cartItem.id, cartItem.id, cartItem.cartId, cartItem.quantityOrdered))
                        }
                        refreshCart()
                    }
                }

                val onRemoveItem: (CartItem) -> Unit = { cartItem ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        foodDao.deleteFoodOrder(FoodOrder(cartItem.id, cartItem.id, cartItem.cartId, cartItem.quantityOrdered))
                        refreshCart()
                    }
                }

                recyclerView.adapter = CartAdapter(cartItems, onIncreaseQuantity, onDecreaseQuantity, onRemoveItem)

                if (cartItems.isEmpty()) {
                    view.findViewById<TextView>(R.id.emptyCartMessage).visibility = View.VISIBLE
                } else {
                    view.findViewById<TextView>(R.id.emptyCartMessage).visibility = View.GONE
                }
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