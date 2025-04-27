package com.example.testversion

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val fullScreenImage = findViewById<ImageView>(R.id.fullscreen_image)
        val imageResId = intent.getIntExtra("imageResId", 0)

        if (imageResId != 0) {
            fullScreenImage.setImageResource(imageResId)
        }

        // Click image to exit
        fullScreenImage.setOnClickListener {
            finish()
        }
    }
}
