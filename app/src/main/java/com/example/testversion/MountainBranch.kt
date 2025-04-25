package com.example.testversion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.testversion.databinding.ActivityMountainBranchBinding
import android.widget.ImageView


class MountainBranch : AppCompatActivity() {

    private lateinit var binding: ActivityMountainBranchBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(R.drawable.mount1)
            .into(binding.imageView1)

        Glide.with(this)
            .load(R.drawable.mount2)
            .into(binding.imageView2)

        Glide.with(this)
            .load(R.drawable.mount3)
            .into(binding.imageView3)

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSelectRoom.setOnClickListener {
            startActivity(Intent(this, MountainRoom::class.java))
        }
    }
}
