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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewBillsActivity : AppCompatActivity() {

    private lateinit var billsRecyclerView: RecyclerView
    private lateinit var noOutstandingBillsText: TextView
    private var bookingId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_bills)

        billsRecyclerView = findViewById(R.id.billsRecyclerView)
        noOutstandingBillsText = findViewById(R.id.noOutstandingBillsText)

        bookingId = intent.getStringExtra("bookingId") ?: ""
        billsRecyclerView.layoutManager = LinearLayoutManager(this)
        fetchAndDisplayServiceUsages()
    }

    private fun fetchAndDisplayServiceUsages() {
        lifecycleScope.launch {
            val serviceDao = AppDatabase.getInstance(this@ViewBillsActivity).serviceDao()

            val serviceUsages: List<ServiceUsage> = withContext(Dispatchers.IO) {
                serviceDao.getServiceUsageByBookingId(bookingId)
            }

            if (serviceUsages.isEmpty()) {
                // Show "No Outstanding Bills" message if no data
                noOutstandingBillsText.visibility = View.VISIBLE
                billsRecyclerView.visibility = View.GONE
            } else {
                // Hide "No Outstanding Bills" message and display data
                noOutstandingBillsText.visibility = View.GONE
                billsRecyclerView.visibility = View.VISIBLE

                // Set up adapter for RecyclerView
                val adapter = BillsAdapter(serviceUsages)
                billsRecyclerView.adapter = adapter
            }
        }
    }
}