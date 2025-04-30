package com.example.testversion

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class BranchOverview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch_overview)


        val mountainCard = findViewById<CardView>(R.id.mountainCard)
        val islandCard = findViewById<CardView>(R.id.islandCard)
        val cityCard = findViewById<CardView>(R.id.cityCard)

        mountainCard.setOnClickListener {
            startActivity(Intent(this, MountainBranch::class.java))
        }
        islandCard.setOnClickListener {
            startActivity(Intent(this, IslandBranch::class.java))
        }
        cityCard.setOnClickListener {
            startActivity(Intent(this, CityBranch::class.java))
        }
    }
}
