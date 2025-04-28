package com.example.testversion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FoodAdapter(
    private val foodItems: List<Food>,
    private val onAddToCartClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private val expandedStates = BooleanArray(foodItems.size) { false }

    inner class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.foodTitle)
        val price: TextView = view.findViewById(R.id.foodPrice)
        val image: ImageView = view.findViewById(R.id.foodImage)
        val addToCartButton: ImageView = view.findViewById(R.id.addToCartButton)
        val expandedLayout: View = view.findViewById(R.id.expandedLayout)
        val description: TextView = view.findViewById(R.id.foodDescription)
        val ingredientsUsed: TextView = view.findViewById(R.id.ingredientsUsed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodItems[position]
        val isExpanded = expandedStates[position]

        holder.title.text = food.name
        holder.price.text = String.format("RM %.2f", food.price)
        holder.image.setImageResource(food.imageResId)
        holder.expandedLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        if (isExpanded) {
            holder.description.text = food.description
            holder.ingredientsUsed.text = food.ingredientsUsed
        }

        holder.itemView.setOnClickListener {
            expandedStates[position] = !isExpanded
            notifyItemChanged(position)
        }

        holder.addToCartButton.setOnClickListener {
            onAddToCartClick(food)
        }
    }

    override fun getItemCount() = foodItems.size
}