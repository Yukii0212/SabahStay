package com.example.testversion

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CityBranch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.city_branch) // Link to city_branch.xml

        // Display a message
        findViewById<TextView>(R.id.cityBranchText).text = "This is City Branch!"
    }
}