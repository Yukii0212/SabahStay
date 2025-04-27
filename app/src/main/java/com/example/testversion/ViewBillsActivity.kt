package com.example.testversion

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.ServiceUsage
import kotlinx.coroutines.launch

class ViewBillsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_bills)

        val billsRecyclerView = findViewById<RecyclerView>(R.id.billsRecyclerView)
        billsRecyclerView.layoutManager = LinearLayoutManager(this)

        val database = AppDatabase.getInstance(this)
        val serviceDao = database.serviceDao()

        lifecycleScope.launch {
            val serviceUsages: List<ServiceUsage> = serviceDao.getServiceUsageByBooking("YOUR_BOOKING_ID")
            val noOutstandingBillsText = findViewById<TextView>(R.id.noOutstandingBillsText)
            val billsRecyclerView = findViewById<RecyclerView>(R.id.billsRecyclerView)

            if (serviceUsages.isEmpty()) {
                noOutstandingBillsText.visibility = View.VISIBLE
                billsRecyclerView.visibility = View.GONE
            } else {
                noOutstandingBillsText.visibility = View.GONE
                billsRecyclerView.visibility = View.VISIBLE
                val adapter = BillsAdapter(serviceUsages)
                billsRecyclerView.adapter = adapter
            }
        }
    }
}