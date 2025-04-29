package com.example.testversion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DessertAdapter(
    private val dessertItems: List<Food>,
    private val onAddToCartClick: (Food) -> Unit
) : RecyclerView.Adapter<DessertAdapter.DessertViewHolder>() {

    private val expandedStates = BooleanArray(dessertItems.size) { false }

    inner class DessertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.foodTitle)
        val price: TextView = view.findViewById(R.id.foodPrice)
        val image: ImageView = view.findViewById(R.id.foodImage)
        val addToCartButton: ImageView = view.findViewById(R.id.addToCartButton)
        val expandedLayout: View = view.findViewById(R.id.expandedLayout)
        val description: TextView = view.findViewById(R.id.foodDescription)
        val ingredientsUsed: TextView = view.findViewById(R.id.ingredientsUsed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DessertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return DessertViewHolder(view)
    }

    override fun onBindViewHolder(holder: DessertViewHolder, position: Int) {
        val dessert = dessertItems[position]
        val isExpanded = expandedStates[position]

        holder.title.text = dessert.name
        holder.price.text = String.format("RM %.2f", dessert.price)
        holder.image.setImageResource(dessert.imageResId)
        holder.expandedLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        if (isExpanded) {
            holder.description.text = dessert.description
            holder.ingredientsUsed.text = dessert.ingredientsUsed
        }

        holder.itemView.setOnClickListener {
            expandedStates[position] = !isExpanded
            notifyItemChanged(position)
        }

        holder.addToCartButton.setOnClickListener {
            onAddToCartClick(dessert)
        }
    }

    override fun getItemCount() = dessertItems.size
}