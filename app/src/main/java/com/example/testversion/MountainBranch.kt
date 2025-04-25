package com.example.testversion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.testversion.R
import com.example.testversion.databinding.ActivityMountainBranchBinding

class MountainBranch : AppCompatActivity() {

    private lateinit var binding: ActivityMountainBranchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_mountain_branch)

        Glide.with(this)
            .load(R.drawable.mount1)
            .circleCrop()
            .into(binding.imageView1)

        Glide.with(this)
            .load(R.drawable.mount2)
            .circleCrop()
            .into(binding.imageView2)

        Glide.with(this)
            .load(R.drawable.mount3)
            .circleCrop()
            .into(binding.imageView3)
    }
}
