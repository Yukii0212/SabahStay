package com.example.testversion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FoodOrderingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val adapter = FoodOrderingPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon = when (position) {
                0 -> getDrawable(R.drawable.food)
                1 -> getDrawable(R.drawable.drinks)
                2 -> getDrawable(R.drawable.alcohol)
                3 -> getDrawable(R.drawable.dessert)
                4 -> getDrawable(R.drawable.ic_cart)
                else -> null
            }
        }.attach()
    }
}