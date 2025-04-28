package com.example.testversion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlcoholFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alcohol, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val database = AppDatabase.getInstance(requireContext())
            val foodDao = database.foodDao()

            // Fetch all alcohol items with category 3
            val alcoholList = withContext(Dispatchers.IO) {
                foodDao.getFoodByCategory(3)
            }

            // Set up the adapter
            val adapter = AlcoholAdapter(alcoholList) { alcohol ->
                Toast.makeText(requireContext(), "${alcohol.name} added to cart", Toast.LENGTH_SHORT).show()
            }
            recyclerView.adapter = adapter
        }

        return view
    }
}