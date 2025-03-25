package com.example.testversion

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.AttributeSet
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
        accountText = findViewById(R.id.account_text)

        updateFooterUI()

        homeSection.setOnClickListener {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }

        reservationSection.setOnClickListener {
            context.startActivity(Intent(context, ReservationActivity::class.java))
        }

        notificationSection.setOnClickListener {
            context.startActivity(Intent(context, NotificationActivity::class.java))
        }

        accountSection.setOnClickListener {
            context.startActivity(Intent(context, UserProfileActivity::class.java))
        }
    }

    fun updateFooterUI() {
        val user = auth.currentUser
        if (user != null) {
            val savedProfilePicturePath = sharedPreferences.getString("profilePicturePath", "") ?: ""
            val savedUsername = sharedPreferences.getString("nickname", user.displayName ?: "User") ?: "User"

            if (savedProfilePicturePath.isNotEmpty() && File(savedProfilePicturePath).exists()) {
                accountIcon.setImageURI(Uri.fromFile(File(savedProfilePicturePath)))
            } else {
                accountIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.account_button))
            }

            accountText.text = savedUsername
        } else {
            accountIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.account_button))
            accountText.text = "Account"
        }
    }
}
