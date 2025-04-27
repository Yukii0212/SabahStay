package com.example.testversion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.ServiceUsage

class BillsAdapter(private val serviceUsages: List<ServiceUsage>) :
    RecyclerView.Adapter<BillsAdapter.BillsViewHolder>() {

    private var expandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_request, parent, false)
        return BillsViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillsViewHolder, position: Int) {
        val serviceUsage = serviceUsages[position]
        holder.serviceTitle.text = serviceUsage.serviceName
        holder.serviceDateTime.text = "${serviceUsage.requestDay} ${serviceUsage.requestTime}"
        holder.servicePrice.text = if (serviceUsage.price == 0.0) "FREE" else "RM${"%.2f".format(serviceUsage.price)}"
        holder.serviceDetails.text = "Room: ${serviceUsage.roomNumber}\nCanceled: ${if (serviceUsage.isCanceled) "Yes" else "No"}"

        val isExpanded = position == expandedPosition
        holder.detailsContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = serviceUsages.size

    class BillsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceTitle: TextView = itemView.findViewById(R.id.serviceTitle)
        val serviceDateTime: TextView = itemView.findViewById(R.id.serviceDateTime)
        val servicePrice: TextView = itemView.findViewById(R.id.servicePrice)
        val serviceDetails: TextView = itemView.findViewById(R.id.serviceDetails)
        val detailsContainer: LinearLayout = itemView.findViewById(R.id.detailsContainer)
    }
}