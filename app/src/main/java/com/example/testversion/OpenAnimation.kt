package com.example.testversion

import android.util.Log // ✅ Import Log
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OpenAnimation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("DEBUG", "MainActivity started")  // ✅ Log that MainActivity is launching

        setContentView(R.layout.main_activity)

        // Load animation resources
        val cloudAnimation = AnimationUtils.loadAnimation(this, R.anim.cloud_move)
        val mountainAnimation = AnimationUtils.loadAnimation(this, R.anim.mountain_zoom)
        val textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade_in)

        Log.d("DEBUG", "Animations loaded")  // ✅ Log that animations are loaded

        // Find views
        val cloudsView: ImageView = findViewById(R.id.cloudsView)
        val cloudsView1: ImageView = findViewById(R.id.cloudsView1)
        val cloudsView2: ImageView = findViewById(R.id.cloudsView2)
        val cloudsView3: ImageView = findViewById(R.id.cloudsView3)

        val mountainView: ImageView = findViewById(R.id.mountainView)
        val titleText: TextView = findViewById(R.id.titleText)
        val subtitleText: TextView = findViewById(R.id.subtitleText)

        Log.d("DEBUG", "Views found")  // ✅ Log that UI elements were found

        // Start animations
        cloudsView.startAnimation(cloudAnimation)
        cloudsView1.startAnimation(cloudAnimation)
        cloudsView2.startAnimation(cloudAnimation)
        cloudsView3.startAnimation(cloudAnimation)
        mountainView.startAnimation(mountainAnimation)

        Log.d("DEBUG", "Animations started")  // ✅ Log that animations have started

        mountainAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // ✅ Show text halfway through the mountain zoom animation
                titleText.postDelayed({
                    Log.d("DEBUG", "Making text visible early")
                    titleText.visibility = View.VISIBLE
                    subtitleText.visibility = View.VISIBLE
                    titleText.startAnimation(textAnimation)
                    subtitleText.startAnimation(textAnimation)
                }, mountainAnimation.duration / 2)
            }

            override fun onAnimationEnd(animation: Animation?) {
                cloudsView.visibility = View.GONE
                cloudsView1.visibility = View.GONE
                cloudsView2.visibility = View.GONE
                cloudsView3.visibility = View.GONE
                mountainView.visibility = View.VISIBLE
            }



            override fun onAnimationRepeat(animation: Animation?) {}
        })



    }
}
