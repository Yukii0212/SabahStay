package com.example.testversion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.testversion.databinding.ActivityIslandBranchBinding

class IslandBranch : AppCompatActivity() {

    private lateinit var binding: ActivityIslandBranchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIslandBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(R.drawable.island1)
            .into(binding.imageView1)

        Glide.with(this)
            .load(R.drawable.island2)
            .into(binding.imageView2)

        Glide.with(this)
            .load(R.drawable.island3)
            .into(binding.imageView3)

        // Back button
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Select Room button
        binding.btnSelectRoom.setOnClickListener {
            startActivity(Intent(this, IslandRoom::class.java))
        }

        // Image click listeners
        binding.imageView1.setOnClickListener {
            openFullScreen(R.drawable.island1)
        }
        binding.imageView2.setOnClickListener {
            openFullScreen(R.drawable.island2)
        }
        binding.imageView3.setOnClickListener {
            openFullScreen(R.drawable.island3)
        }
    }

    private fun openFullScreen(imageResId: Int) {
        val intent = Intent(this, FullscreenImageActivity::class.java)
        intent.putExtra("imageResId", imageResId)
        startActivity(intent)
    }
}
