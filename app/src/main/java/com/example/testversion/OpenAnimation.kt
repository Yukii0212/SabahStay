package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OpenAnimation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("DEBUG", "OpenAnimation started")

        setContentView(R.layout.main_activity)

        // Load animations
        val cloudAnimation = AnimationUtils.loadAnimation(this, R.anim.cloud_move)
        val mountainAnimation = AnimationUtils.loadAnimation(this, R.anim.mountain_zoom)
        val textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade_in)

        Log.d("DEBUG", "Animations loaded")

        // Find views
        val cloudsView: ImageView = findViewById(R.id.cloudsView)
        val cloudsView1: ImageView = findViewById(R.id.cloudsView1)
        val cloudsView2: ImageView = findViewById(R.id.cloudsView2)
        val cloudsView3: ImageView = findViewById(R.id.cloudsView3)

        val mountainView: ImageView = findViewById(R.id.mountainView)
        val titleText: TextView = findViewById(R.id.titleText)
        val subtitleText: TextView = findViewById(R.id.subtitleText)

        Log.d("DEBUG", "Views found")

        // Start animations
        cloudsView.startAnimation(cloudAnimation)
        cloudsView1.startAnimation(cloudAnimation)
        cloudsView2.startAnimation(cloudAnimation)
        cloudsView3.startAnimation(cloudAnimation)
        mountainView.startAnimation(mountainAnimation)

        Log.d("DEBUG", "Animations started")

        mountainAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // Show text halfway through the animation
                titleText.postDelayed({
                    Log.d("DEBUG", "Showing text animations")
                    titleText.visibility = View.VISIBLE
                    subtitleText.visibility = View.VISIBLE
                    titleText.startAnimation(textAnimation)
                    subtitleText.startAnimation(textAnimation)
                }, mountainAnimation.duration / 2)
            }

            override fun onAnimationEnd(animation: Animation?) {
                Log.d("DEBUG", "Animation finished, transitioning to FakeMainActivity")

                // Hide clouds after animation
                cloudsView.visibility = View.GONE
                cloudsView1.visibility = View.GONE
                cloudsView2.visibility = View.GONE
                cloudsView3.visibility = View.GONE
                mountainView.visibility = View.VISIBLE

                // Move to FakeMainActivity
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@OpenAnimation, BranchOverview::class.java)
                    startActivity(intent)
                    finish() // Close animation screen
                }, 1000) // Short delay before switching screen
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}
