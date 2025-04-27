package com.example.testversion

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewBillsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_bills)

        val bookingId = intent.getIntExtra("bookingId", 0)
        Log.d("ViewBillsActivity", "Received bookingId: $bookingId")

        val billsRecyclerView = findViewById<RecyclerView>(R.id.billsRecyclerView)
        billsRecyclerView.layoutManager = LinearLayoutManager(this)

        val noBillsTextView = findViewById<TextView>(R.id.noBillsTextView)

        lifecycleScope.launch {
            val serviceUsages = withContext(Dispatchers.IO) {
                val serviceDao = AppDatabase.getInstance(this@ViewBillsActivity).serviceDao()
                serviceDao.getServiceUsageByBookingId(bookingId.toString())
            }

            if (serviceUsages.isEmpty()) {
                noBillsTextView.visibility = View.VISIBLE
            } else {
                noBillsTextView.visibility = View.GONE
                billsRecyclerView.adapter = BillsAdapter(serviceUsages, this@ViewBillsActivity)

                val total = serviceUsages.sumOf { it.price }
                val totalTextView = findViewById<TextView>(R.id.totalTextView)
                totalTextView.text = if (total == 0.0) "Total: FREE" else String.format("Total: RM%.2f", total)
            }
        }
    }
}