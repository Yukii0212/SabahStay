package com.example.testversion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.testversion.databinding.ActivityMountainBranchBinding

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

        // Back button
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Select Room button
        binding.btnSelectRoom.setOnClickListener {
            startActivity(Intent(this, MountainRoom::class.java))
        }

        // Image click listeners
        binding.imageView1.setOnClickListener {
            openFullScreen(R.drawable.mount1)
        }
        binding.imageView2.setOnClickListener {
            openFullScreen(R.drawable.mount2)
        }
        binding.imageView3.setOnClickListener {
            openFullScreen(R.drawable.mount3)
        }
    }

    // Move this function OUTSIDE of onCreate()
    private fun openFullScreen(imageResId: Int) {
        val intent = Intent(this, FullscreenImageActivity::class.java)
        intent.putExtra("imageResId", imageResId)
        startActivity(intent)
    }
}
