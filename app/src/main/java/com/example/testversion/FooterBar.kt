package com.example.testversion

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
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
    private var serviceSection: LinearLayout
    private var accountSection: LinearLayout
    private var accountIcon: ImageView
    private var accountText: TextView
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

    init {
        LayoutInflater.from(context).inflate(R.layout.footer_bar, this, true)

        homeSection = findViewById(R.id.home_section)
        reservationSection = findViewById(R.id.reservation_section)
        serviceSection = findViewById(R.id.service_section)
        accountSection = findViewById(R.id.account_section)
        accountIcon = findViewById(R.id.account_icon)
        accountText = findViewById(R.id.account_text)

        updateFooterUI()

        homeSection.setOnClickListener{
            val intent = Intent(context, BranchOverview::class.java)
            context.startActivity(intent)
        }

        reservationSection.setOnClickListener {
            val intent = Intent(context, ReservationActivity::class.java)
            context.startActivity(intent)
        }

        serviceSection.setOnClickListener {
            val intent = Intent(context, ServiceActivity::class.java)
            context.startActivity(intent)
        }

        accountSection.setOnClickListener {
            val user = auth.currentUser
            Log.d("FooterBar", "User is ${user?.uid ?: "null"}")

            val intent = if (user == null) {
                Intent(context, LoginActivity::class.java)
            } else {
                Intent(context, UserProfileActivity::class.java)
            }
            context.startActivity(intent)
        }
    }

    private fun updateFooterUI() {
        val user = auth.currentUser
        if (user != null) {
            val savedProfilePicturePath = sharedPreferences.getString("profilePicturePath", "") ?: ""
            val savedUsername = sharedPreferences.getString("nickname", user.displayName) ?: user.displayName ?: "User"

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