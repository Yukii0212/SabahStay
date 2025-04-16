package com.example.testversion

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.AttributeSet
import android.util.Log // Import added for Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class FooterBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var homeSection: LinearLayout
    private var reservationSection: LinearLayout
    private var notificationSection: LinearLayout
    private var accountSection: LinearLayout
    private var accountIcon: ImageView
    private var accountText: TextView
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

    init {
        LayoutInflater.from(context).inflate(R.layout.footer_bar, this, true)

        homeSection = findViewById(R.id.home_section)
        reservationSection = findViewById(R.id.reservation_section)
        notificationSection = findViewById(R.id.notification_section)
        accountSection = findViewById(R.id.account_section)
        accountIcon = findViewById(R.id.account_icon)
        accountText = findViewById(R.id.profileButton) // Corrected id reference

        updateFooterUI()

        accountSection.setOnClickListener {
            Log.d("FooterBar", "Account section clicked")
            val user = auth.currentUser
            if (user == null) {
                context.startActivity(Intent(context, LoginActivity::class.java))
            } else {
                context.startActivity(Intent(context, UserProfileActivity::class.java))
            }
        }
    }

    fun updateFooterUI() {
        val user = auth.currentUser
        if (user != null) {
            val savedProfilePicturePath = sharedPreferences.getString("profilePicturePath", "") ?: ""
            val savedUsername = sharedPreferences.getString("nickname", user.displayName ?: "User") ?: "User"

            // Set profile picture if available, otherwise use default icon
            if (savedProfilePicturePath.isNotEmpty() && File(savedProfilePicturePath).exists()) {
                accountIcon.setImageURI(Uri.fromFile(File(savedProfilePicturePath)))
            } else {
                accountIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.account_button))
            }

            // Set text to "<user name>'s account"
            accountText.text = "$savedUsername's account"
        } else {
            // Default icon and text for no user
            accountIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.account_button))
            accountText.text = "Account"
        }
    }
}