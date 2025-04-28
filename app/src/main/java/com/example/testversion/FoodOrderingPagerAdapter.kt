package com.example.testversion

import CartFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FoodOrderingPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FoodFragment()
            1 -> DrinksFragment()
            2 -> AlcoholFragment()
            3 -> DessertFragment()
            4 -> CartFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}