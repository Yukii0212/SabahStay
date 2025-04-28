package com.example.testversion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlcoholAdapter(
    private val alcoholItems: List<Food>,
    private val onAddToCartClick: (Food) -> Unit
) : RecyclerView.Adapter<AlcoholAdapter.AlcoholViewHolder>() {

    private val expandedStates = BooleanArray(alcoholItems.size) { false }

    inner class AlcoholViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.foodTitle)
        val price: TextView = view.findViewById(R.id.foodPrice)
        val image: ImageView = view.findViewById(R.id.foodImage)
        val addToCartButton: ImageView = view.findViewById(R.id.addToCartButton)
        val expandedLayout: View = view.findViewById(R.id.expandedLayout)
        val description: TextView = view.findViewById(R.id.foodDescription)
        val ingredientsUsed: TextView = view.findViewById(R.id.ingredientsUsed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoholViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return AlcoholViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlcoholViewHolder, position: Int) {
        val alcohol = alcoholItems[position]
        val isExpanded = expandedStates[position]

        holder.title.text = alcohol.name
        holder.price.text = String.format("RM %.2f", alcohol.price)
        holder.image.setImageResource(alcohol.imageResId)
        holder.expandedLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        if (isExpanded) {
            holder.description.text = alcohol.description
            holder.ingredientsUsed.text = alcohol.ingredientsUsed
        }

        holder.itemView.setOnClickListener {
            expandedStates[position] = !isExpanded
            notifyItemChanged(position)
        }

        holder.addToCartButton.setOnClickListener {
            onAddToCartClick(alcohol)
        }
    }

    override fun getItemCount() = alcoholItems.size
}