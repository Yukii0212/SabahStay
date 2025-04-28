package com.example.testversion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast

class DrinksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drinks, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val database = AppDatabase.getInstance(requireContext())
            val foodDao = database.foodDao()

            // Fetch all drink items with category 2
            val drinksList = withContext(Dispatchers.IO) {
                foodDao.getFoodByCategory(2)
            }

            // Set up the adapter
            val adapter = DrinksAdapter(drinksList) { drink ->
                Toast.makeText(requireContext(), "${drink.name} added to cart", Toast.LENGTH_SHORT).show()
            }
            recyclerView.adapter = adapter
        }

        return view
    }
}