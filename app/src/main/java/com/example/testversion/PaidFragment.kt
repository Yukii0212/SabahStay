package com.example.testversion

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

class PaidFragment : Fragment(R.layout.fragment_paid) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookingId = requireActivity().intent.getIntExtra("bookingId", 0)
        val recyclerView = view.findViewById<RecyclerView>(R.id.paidRecyclerView)
        val noBillsTextView = view.findViewById<TextView>(R.id.noBillsTextView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val serviceUsages = withContext(Dispatchers.IO) {
                val serviceDao = AppDatabase.getInstance(requireContext()).serviceDao()
                serviceDao.getPaidBillsForBooking(bookingId)
                    .filter { it.isPaid }
            }

            if (serviceUsages.isEmpty()) {
                noBillsTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noBillsTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter = BillsAdapter(serviceUsages, viewLifecycleOwner)
            }
        }
    }
}