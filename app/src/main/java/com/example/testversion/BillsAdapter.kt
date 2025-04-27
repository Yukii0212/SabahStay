package com.example.testversion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.ServiceUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillsAdapter(
    private val serviceUsages: List<ServiceUsage>,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_request, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val serviceUsage = serviceUsages[position]

        // Collapsed version
        holder.serviceTitle.text = serviceUsage.serviceName
        holder.servicePrice.text = if (serviceUsage.price == 0.0) "FREE" else String.format("RM%.2f", serviceUsage.price)
        holder.serviceDateTime.visibility = View.GONE

        // Expanded version
        holder.itemView.setOnClickListener {
            val isVisible = holder.detailsContainer.visibility == View.VISIBLE
            if (!isVisible) {
                lifecycleOwner.lifecycleScope.launch {
                    val bookingDetails = withContext(Dispatchers.IO) {
                        val database = AppDatabase.getInstance(holder.itemView.context)
                        val finalizedBooking = database.finalizedBookingDao().getByBookingNumber(serviceUsage.bookingId.toLong())
                        val user = finalizedBooking?.userEmail?.let { email ->
                            database.userDao().getUserByEmail(email)
                        }
                        Triple(finalizedBooking?.branchName, user?.name, finalizedBooking?.roomType)
                    }

                    holder.serviceDetails.text = """
                    Room Number: ${serviceUsage.roomNumber}
                    Requestee: ${bookingDetails.second ?: "Unknown"}
                    Date: ${serviceUsage.requestDay}
                    Time: ${serviceUsage.requestTime}
                    Branch: ${bookingDetails.first ?: "Unknown"}
                    Room Type: ${bookingDetails.third ?: "Unknown"}
                    Cost Breakdown:
                    - Service: ${if (serviceUsage.price == 0.0) "FREE" else String.format("RM%.2f", serviceUsage.price)}
                """.trimIndent()
                    holder.detailsContainer.visibility = View.VISIBLE
                }
            } else {
                holder.detailsContainer.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = serviceUsages.size

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceTitle: TextView = itemView.findViewById(R.id.serviceTitle)
        val serviceDateTime: TextView = itemView.findViewById(R.id.serviceDateTime)
        val servicePrice: TextView = itemView.findViewById(R.id.servicePrice)
        val serviceDetails: TextView = itemView.findViewById(R.id.serviceDetails)
        val detailsContainer: View = itemView.findViewById(R.id.detailsContainer)
    }
}