import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.CartItem
import com.example.testversion.R

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onItemAction: (CartItem, ActionType) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    enum class ActionType { INCREASE, DECREASE, REMOVE }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.cartItemImage)
        val itemTitle: TextView = itemView.findViewById(R.id.cartItemTitle)
        val itemPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
        val itemQuantity: TextView = itemView.findViewById(R.id.cartItemQuantity)
        val increaseButton: Button = itemView.findViewById(R.id.increaseQuantityButton)
        val decreaseButton: Button = itemView.findViewById(R.id.decreaseQuantityButton)
        val removeButton: ImageView = itemView.findViewById(R.id.removeFromCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.itemTitle.text = cartItem.name
        holder.itemPrice.text = "RM ${cartItem.price}"
        holder.itemQuantity.text = "Quantity: ${cartItem.quantityOrdered}"
        holder.itemImage.setImageResource(cartItem.imageResId)

        holder.increaseButton.setOnClickListener {
            onItemAction(cartItem, ActionType.INCREASE)
        }

        holder.decreaseButton.setOnClickListener {
            onItemAction(cartItem, ActionType.DECREASE)
        }

        holder.removeButton.setOnClickListener {
            onItemAction(cartItem, ActionType.REMOVE)
        }
    }

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newCartItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = cartItems.size
}