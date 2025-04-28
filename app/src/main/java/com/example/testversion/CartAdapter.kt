package com.example.testversion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface OnQuantityChangeListener {
    fun onQuantityChanged(position: Int, newQuantity: Int)
}

class CartAdapter(
    private val cartItems: List<FoodOrder>,
    private val foodMap: Map<Int, Food>, // Map to get Food details by foodId
    private val listener: OnQuantityChangeListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.cartItemTitle)
        val price: TextView = itemView.findViewById(R.id.cartItemPrice)
        val quantity: TextView = itemView.findViewById(R.id.cartItemQuantity)
        val increaseButton: Button = itemView.findViewById(R.id.increaseQuantityButton)
        val decreaseButton: Button = itemView.findViewById(R.id.decreaseQuantityButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val order = cartItems[position]
        val food = foodMap[order.foodId] ?: return

        holder.title.text = food.name
        holder.quantity.text = "Quantity: ${order.quantityOrdered}"
        holder.price.text = "RM ${food.price * order.quantityOrdered}"

        holder.increaseButton.setOnClickListener {
            val newQuantity = order.quantityOrdered + 1
            listener.onQuantityChanged(position, newQuantity)
        }

        holder.decreaseButton.setOnClickListener {
            if (order.quantityOrdered > 1) {
                val newQuantity = order.quantityOrdered - 1
                listener.onQuantityChanged(position, newQuantity)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size
}