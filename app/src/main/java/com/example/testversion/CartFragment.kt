import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.BranchOverview
import com.example.testversion.CartItem
import com.example.testversion.FoodCart
import com.example.testversion.FoodDao
import com.example.testversion.R
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.ServiceUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyCartMessage: TextView
    private lateinit var cartTotalLabel: TextView
    private lateinit var taxLabel: TextView
    private lateinit var subtotalLabel: TextView
    private var cartItems: MutableList<CartItem> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmOrderButton = view.findViewById<Button>(R.id.confirmOrderButton)
        val deliveryDateInput = view.findViewById<EditText>(R.id.deliveryDateInput)
        val deliveryTimeInput = view.findViewById<EditText>(R.id.deliveryTimeInput)


        // Retrieve bookingId from intent
        val bookingId = requireActivity().intent.getIntExtra("bookingId", -1)
        if (bookingId == -1) {
            Toast.makeText(requireContext(), "Invalid booking ID", Toast.LENGTH_SHORT).show()
            return
        }

        deliveryDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "${selectedDay.toString().padStart(2, '0')}/" +
                            "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                    deliveryDateInput.setText(formattedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        deliveryTimeInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val isPM = selectedHour >= 12
                    val formattedHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                    val formattedTime = String.format(
                        "%02d:%02d %s",
                        formattedHour,
                        selectedMinute,
                        if (isPM) "PM" else "AM"
                    )
                    deliveryTimeInput.setText(formattedTime)
                },
                hour,
                minute,
                false // Use analog clock
            ).show()
        }

        confirmOrderButton.setOnClickListener {
            val total = cartItems.sumOf { it.price * it.quantityOrdered }
            val message = "Your total is RM %.2f. Do you want to submit the booking?".format(total)

            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Booking")
                .setMessage(message)
                .setPositiveButton("Yes") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val database = AppDatabase.getInstance(requireContext())
                            val foodDao = database.foodDao()
                            val serviceDao = database.serviceDao()

                            // Clear the cart
                            foodDao.clearCart(1)

                            // Insert into ServiceUsage
                            val serviceUsage = ServiceUsage(
                                bookingId = bookingId.toString(),
                                roomNumber = null.toString(),
                                serviceId = 2,
                                serviceName = "Food Order",
                                price = total,
                                requestTime = null.toString(),
                                requestDay = null.toString(),
                                isCanceled = false
                            )
                            serviceDao.insertServiceUsage(serviceUsage)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Booking submitted successfully!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), BranchOverview::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        } catch (e: Exception) {
                            Log.e("CartFragment", "Error submitting booking", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Failed to submit booking", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val confirmOrderButton = view.findViewById<Button>(R.id.confirmOrderButton)

        recyclerView = view.findViewById(R.id.cartRecyclerView)
        emptyCartMessage = view.findViewById(R.id.emptyCartMessage)
        cartTotalLabel = view.findViewById(R.id.cartTotalLabel)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadCartData()

        parentFragmentManager.setFragmentResultListener("cartUpdated", this) { _, _ ->
            Log.d("CartFragment", "cartUpdated event received")
            loadCartData()
        }

        confirmOrderButton.setOnClickListener {
            val total = cartItems.sumOf { it.price * it.quantityOrdered }
            val message = "Your total is RM %.2f. Do you want to submit the booking?".format(total)

            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Booking")
                .setMessage(message)
                .setPositiveButton("Yes") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val database = AppDatabase.getInstance(requireContext())
                            val foodDao = database.foodDao()
                            val serviceDao = database.serviceDao()

                            foodDao.clearCart(1)

                            val serviceUsage = ServiceUsage(
                                bookingId = "1",
                                roomNumber = null.toString(),
                                serviceId = 2,
                                serviceName = "Food Order",
                                price = total,
                                requestTime = null.toString(),
                                requestDay = null.toString(),
                                isCanceled = false
                            )
                            serviceDao.insertServiceUsage(serviceUsage)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Booking submitted successfully!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), BranchOverview::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        } catch (e: Exception) {
                            Log.e("CartFragment", "Error submitting booking", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Failed to submit booking", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
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