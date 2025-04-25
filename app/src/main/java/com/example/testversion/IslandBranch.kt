package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class IslandBranch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_island_branch)

        // Button click to go to IslandRoom activity
        val selectRoomButton = findViewById<Button>(R.id.btn_select_room)
        selectRoomButton.setOnClickListener {
            val intent = Intent(this, IslandRoom::class.java)
            startActivity(intent)
        }
    }
}
