import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.R
import com.example.testversion.CartItem

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onRemove: (CartItem) -> Unit,
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.cartItemTitle)
        val price = view.findViewById<TextView>(R.id.cartItemPrice)
        val quantity = view.findViewById<TextView>(R.id.cartItemQuantity)
        val increaseButton = view.findViewById<Button>(R.id.increaseQuantityButton)
        val decreaseButton = view.findViewById<Button>(R.id.decreaseQuantityButton)
        val removeButton = view.findViewById<ImageView>(R.id.removeFromCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.title.text = item.name
        holder.price.text = "RM${item.price}"
        holder.quantity.text = "Quantity: ${item.quantityOrdered}"

        holder.increaseButton.setOnClickListener { onIncrease(item) }
        holder.decreaseButton.setOnClickListener { onDecrease(item) }
        holder.removeButton.setOnClickListener { onRemove(item) }
    }

    override fun getItemCount(): Int = cartItems.size
}