package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OutstandingFragment : Fragment(R.layout.fragment_outstanding) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookingId = requireActivity().intent.getIntExtra("bookingId", 0)
        val recyclerView = view.findViewById<RecyclerView>(R.id.outstandingRecyclerView)
        val noBillsTextView = view.findViewById<TextView>(R.id.noBillsTextView)
        val totalTextView = view.findViewById<TextView>(R.id.totalTextView)
        val payBillsButton = view.findViewById<View>(R.id.payBillsButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val serviceUsages = withContext(Dispatchers.IO) {
                val serviceDao = AppDatabase.getInstance(requireContext()).serviceDao()
                serviceDao.getServiceUsageByBookingId(bookingId.toString())
                    .filter { !it.isPaid }
            }

            if (serviceUsages.isEmpty()) {
                noBillsTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                totalTextView.visibility = View.GONE
                payBillsButton.visibility = View.GONE
            } else {
                noBillsTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter = BillsAdapter(serviceUsages, viewLifecycleOwner)

                val total = serviceUsages.sumOf { it.price }
                totalTextView.text = "Total: RM${"%.2f".format(total)}"
                totalTextView.visibility = View.VISIBLE
                payBillsButton.visibility = View.VISIBLE

                payBillsButton.setOnClickListener {
                    val intent = Intent(requireContext(), PayBillsActivity::class.java)
                    intent.putExtra("totalAmount", "%.2f".format(total))
                    startActivity(intent)
                }
            }
        }
    }
}