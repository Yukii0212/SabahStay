package com.SabahStay.app.anim

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.open_animation.*

class OpenAnimation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.open_animation)

        // Load the animation
        val cloudAnimation = AnimationUtils.loadAnimation(this, R.anim.CloudAnimation)

        // Apply the animation to each cloud
        cloud1.startAnimation(cloudAnimation)
        cloud2.startAnimation(cloudAnimation)
        cloud3.startAnimation(cloudAnimation)
    }
}