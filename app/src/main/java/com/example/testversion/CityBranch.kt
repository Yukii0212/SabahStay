package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CityBranch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_branch) // Your updated layout file

        val selectRoomButton: Button = findViewById(R.id.btn_select_room)
        selectRoomButton.setOnClickListener {
            val intent = Intent(this, CityRoom::class.java)
            startActivity(intent)
        }
    }
}
