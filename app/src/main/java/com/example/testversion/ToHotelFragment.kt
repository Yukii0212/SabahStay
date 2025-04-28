package com.example.testversion

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.AppDatabase
import com.example.testversion.database.ServiceUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ToHotelFragment : Fragment() {
    private var bookingNumber: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_to_hotel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Existing code
        Log.d("ToHotelFragment", "onViewCreated called")

        val vehicleSpinner: Spinner = view.findViewById(R.id.vehicleSpinner)
        val vehicleOptions = arrayOf("Shuttle", "Taxi")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, vehicleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vehicleSpinner.adapter = adapter

        bookingNumber = requireActivity().intent.getIntExtra("bookingId", -1)
        if (bookingNumber != -1) {
            fetchBranchName(bookingNumber, view)
        }

        val priceEditText: EditText = view.findViewById(R.id.priceEditText)
        vehicleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedVehicle = vehicleOptions[position]
                updatePrice(priceEditText, selectedVehicle)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // New functionality for pickup date and time
        val pickupDateEditText: EditText = view.findViewById(R.id.pickupDateEditText)
        val pickupTimeEditText: EditText = view.findViewById(R.id.pickupTimeEditText)

        // Set up DatePickerDialog for pickup date
        pickupDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "${selectedDay.toString().padStart(2, '0')}/" +
                            "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                    pickupDateEditText.setText(formattedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        // Set up TimePickerDialog for pickup time
        pickupTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val isPM = selectedHour >= 12
                    val formattedHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                    val formattedTime = String.format(
                        "%02d:%02d %s",
                        formattedHour,
                        selectedMinute,
                        if (isPM) "PM" else "AM"
                    )
                    pickupTimeEditText.setText(formattedTime)
                },
                hour,
                minute,
                false // Use analog clock
            ).show()
        }

        val submitBookingButton: Button = view.findViewById(R.id.submitBookingRequestButton)

        submitBookingButton.setOnClickListener {
            val branchName = view.findViewById<EditText>(R.id.branchEditText).text.toString()
            val pickupDate = view.findViewById<EditText>(R.id.pickupDateEditText).text.toString()
            val pickupTime = view.findViewById<EditText>(R.id.pickupTimeEditText).text.toString()
            val selectedVehicle = vehicleSpinner.selectedItem.toString()

            if (branchName.isEmpty() || pickupDate.isEmpty() || pickupTime.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = when {
                branchName == "KK City" && selectedVehicle == "Shuttle" -> 20.0
                branchName == "KK City" && selectedVehicle == "Taxi" -> 50.0
                branchName in listOf("Kundasang", "Island Branch") && selectedVehicle == "Shuttle" -> 50.0
                branchName in listOf("Kundasang", "Island Branch") && selectedVehicle == "Taxi" -> 100.0
                else -> 0.0
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Booking Request")
                .setMessage("Do you want to submit this booking request?")
                .setPositiveButton("Confirm") { _, _ ->
                    lifecycleScope.launch {
                        try {
                            val serviceDao = AppDatabase.getInstance(requireContext()).serviceDao()
                            withContext(Dispatchers.IO) {
                                val serviceUsage = ServiceUsage(
                                    bookingId = bookingNumber.toString(),
                                    roomNumber = null.toString(),
                                    serviceId = 4,
                                    serviceName = "Hotel Transfer",
                                    price = price,
                                    requestTime = pickupTime,
                                    requestDay = pickupDate,
                                    isCanceled = false,
                                    cleaningRequestCount = 0
                                )
                                serviceDao.insertServiceUsage(serviceUsage)
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Booking request submitted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(requireContext(), BranchOverview::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        } catch (e: Exception) {
                            Log.e("ToHotelFragment", "Error inserting booking data", e)
                        }
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun updatePrice(priceEditText: EditText, vehicleType: String) {
        val branchName = view?.findViewById<EditText>(R.id.branchEditText)?.text.toString()
        val price = when {
            branchName == "KK City" && vehicleType == "Shuttle" -> 20
            branchName == "KK City" && vehicleType == "Taxi" -> 50
            branchName in listOf("Kundasang", "Island Branch") && vehicleType == "Shuttle" -> 50
            branchName in listOf("Kundasang", "Island Branch") && vehicleType == "Taxi" -> 100
            else -> 0
        }
        priceEditText.setText("RM $price")
    }

    private fun fetchBranchName(bookingNumber: Int, view: View) {
        val dao = AppDatabase.getInstance(requireContext()).finalizedBookingDao()
        Log.d("ToHotelFragment", "DAO initialized: $dao")

        lifecycleScope.launch {
            val branchName = withContext(Dispatchers.IO) {
                dao.getBranchByBookingNumber(bookingNumber)
            }
            Log.d("ToHotelFragment", "Booking Number: $bookingNumber, Fetched Branch Name: $branchName")
            branchName?.let {
                view.findViewById<EditText>(R.id.branchEditText).setText(it)
            } ?: Log.d("ToHotelFragment", "Branch name is null for Booking Number: $bookingNumber")
        }
    }
}