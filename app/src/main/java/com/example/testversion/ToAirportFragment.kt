package com.example.testversion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

class ToAirportFragment : Fragment() {
    private var bookingNumber: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_to_airport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ToAirportFragment", "onViewCreated called")

        // Initialize Spinner
        val vehicleSpinner: Spinner = view.findViewById(R.id.vehicleSpinner)
        val vehicleOptions = arrayOf("Shuttle", "Taxi")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, vehicleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vehicleSpinner.adapter = adapter

        // Fetch branch name
        bookingNumber = requireActivity().intent.getIntExtra("bookingId", -1)
        Log.d("ToAirportFragment", "Booking Number from Arguments: $bookingNumber")
        if (bookingNumber != -1) {
            fetchBranchName(bookingNumber, view)
        } else {
            Log.d("ToAirportFragment", "Invalid Booking Number: $bookingNumber")
        }
    }

    private fun fetchBranchName(bookingNumber: Int, view: View) {
        val dao = AppDatabase.getInstance(requireContext()).finalizedBookingDao()
        Log.d("ToAirportFragment", "DAO initialized: $dao")

        lifecycleScope.launch {
            val branchName = withContext(Dispatchers.IO) {
                dao.getBranchByBookingNumber(bookingNumber)
            }
            Log.d("ToAirportFragment", "Booking Number: $bookingNumber, Fetched Branch Name: $branchName")
            branchName?.let {
                view.findViewById<EditText>(R.id.branchEditText).setText(it)
            } ?: Log.d("ToAirportFragment", "Branch name is null for Booking Number: $bookingNumber")
        }
    }
}