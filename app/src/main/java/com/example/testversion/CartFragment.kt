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
import com.example.testversion.R
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyCartMessage: TextView
    private lateinit var cartTotalLabel: TextView
    private lateinit var taxLabel: TextView
    private lateinit var subtotalLabel: TextView
    private var cartItems: MutableList<CartItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.cartRecyclerView)
        emptyCartMessage = view.findViewById(R.id.emptyCartMessage)
        cartTotalLabel = view.findViewById(R.id.cartTotalLabel)
        taxLabel = view.findViewById(R.id.taxLabel)
        subtotalLabel = view.findViewById(R.id.subtotalLabel)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadCartData()

        parentFragmentManager.setFragmentResultListener("cartUpdated", this) { _, _ ->
            Log.d("CartFragment", "cartUpdated event received")
            loadCartData()
        }

        return view
    }

    private fun loadCartData() {
        lifecycleScope.launch {
            val database = AppDatabase.getInstance(requireContext())
            val foodDao = database.foodDao()

            try {
                val cartId = withContext(Dispatchers.IO) { getOrCreateCartId(foodDao) }
                val foodOrders = withContext(Dispatchers.IO) { foodDao.getOrdersByCartId(cartId) }

                cartItems = withContext(Dispatchers.IO) {
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
                    }.toMutableList()
                }

                Log.d("CartFragment", "Cart items loaded: ${cartItems.size}")
                activity?.runOnUiThread {
                    updateUI()
                }
            } catch (e: Exception) {
                Log.e("CartFragment", "Error fetching cart items", e)
                Toast.makeText(requireContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        if (cartItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyCartMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyCartMessage.visibility = View.GONE

            // Always refresh the adapter with the latest data
            if (recyclerView.adapter == null) {
                recyclerView.adapter = CartAdapter(cartItems) { cartItem, actionType ->
                    handleCartAction(cartItem, actionType)
                }
            } else {
                (recyclerView.adapter as CartAdapter).updateCartItems(cartItems)
            }
        }
        calculateCartSummary()
    }

    private fun handleCartAction(cartItem: CartItem, actionType: CartAdapter.ActionType) {
        val database = AppDatabase.getInstance(requireContext())
        val foodDao = database.foodDao()

        lifecycleScope.launch(Dispatchers.IO) {
            when (actionType) {
                CartAdapter.ActionType.INCREASE -> {
                    cartItem.quantityOrdered++
                    foodDao.updateOrderQuantity(cartItem.id, cartItem.quantityOrdered)
                }
                CartAdapter.ActionType.DECREASE -> {
                    if (cartItem.quantityOrdered > 1) {
                        cartItem.quantityOrdered--
                        foodDao.updateOrderQuantity(cartItem.id, cartItem.quantityOrdered)
                    } else {
                        foodDao.deleteOrder(cartItem.id)
                    }
                }
                CartAdapter.ActionType.REMOVE -> {
                    foodDao.deleteOrder(cartItem.id)
                }
            }
            
            val cartId = getOrCreateCartId(foodDao)
            val foodOrders = foodDao.getOrdersByCartId(cartId)
            cartItems = foodOrders.map { foodOrder ->
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
            }.toMutableList()

            withContext(Dispatchers.Main) {
                updateUI()
            }
        }
    }

    private suspend fun getOrCreateCartId(foodDao: FoodDao): Int {
        val existingCart = foodDao.getCartById(1)
        return existingCart?.id ?: foodDao.createCart(FoodCart()).toInt()
    }

    private fun calculateCartSummary() {
        val total = cartItems.sumOf { it.price * it.quantityOrdered }
        val tax = total * 0.1
        val subtotal = total + tax

        cartTotalLabel.text = "Cart Total: RM %.2f".format(total)
        taxLabel.text = "Tax: RM %.2f".format(tax)
        subtotalLabel.text = "Subtotal: RM %.2f".format(subtotal)
    }
}