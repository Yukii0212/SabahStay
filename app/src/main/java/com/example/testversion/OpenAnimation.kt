package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OpenAnimation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_animation)

        // Load animations
        val cloudMoveLeft = AnimationUtils.loadAnimation(this, R.anim.cloud_move_left)
        val cloudMoveRight = AnimationUtils.loadAnimation(this, R.anim.cloud_move_right)
        val mountainZoom = AnimationUtils.loadAnimation(this, R.anim.mountain_zoom)
        val textFadeIn = AnimationUtils.loadAnimation(this, R.anim.text_fade_in)

        // Find views
        val cloud1: ImageView = findViewById(R.id.cloud1)
        val cloud2: ImageView = findViewById(R.id.cloud2)
        val cloud3: ImageView = findViewById(R.id.cloud3)
        val cloud4: ImageView = findViewById(R.id.cloud4)
        val cloud5: ImageView = findViewById(R.id.cloud5)
        val cloud6: ImageView = findViewById(R.id.cloud6)
        val cloud7: ImageView = findViewById(R.id.cloud7)
        val cloud8: ImageView = findViewById(R.id.cloud8)

        val mountainView: ImageView = findViewById(R.id.mountainImageView)
        val titleText: TextView = findViewById(R.id.titleText)
        val subtitleText: TextView = findViewById(R.id.subtitleText)

        mountainView.visibility = View.VISIBLE

        // Start cloud animations
        cloud1.startAnimation(cloudMoveLeft)
        cloud2.startAnimation(cloudMoveRight)
        cloud3.startAnimation(cloudMoveLeft)
        cloud4.startAnimation(cloudMoveRight)
        cloud5.startAnimation(cloudMoveLeft)
        cloud6.startAnimation(cloudMoveRight)
        cloud7.startAnimation(cloudMoveLeft)
        cloud8.startAnimation(cloudMoveRight)

        // Ensure clouds fully disperse
        val cloudAnimationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                cloud1.visibility = View.GONE
                cloud2.visibility = View.GONE
                cloud3.visibility = View.GONE
                cloud4.visibility = View.GONE
                cloud5.visibility = View.GONE
                cloud6.visibility = View.GONE
                cloud7.visibility = View.GONE
                cloud8.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        }

        cloudMoveLeft.setAnimationListener(cloudAnimationListener)
        cloudMoveRight.setAnimationListener(cloudAnimationListener)

        // Start mountain zoom animation
        mountainView.startAnimation(mountainZoom)

        // Make text **visible** at the right time
        Handler(Looper.getMainLooper()).postDelayed({
            titleText.visibility = View.VISIBLE
            subtitleText.visibility = View.VISIBLE
            titleText.startAnimation(textFadeIn)
            subtitleText.startAnimation(textFadeIn)
        }, (mountainZoom.duration * 0.6 - 1100).toLong())

        // Transition to next activity after animation
        mountainZoom.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
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
