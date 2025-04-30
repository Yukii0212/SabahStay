package com.example.testversion

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profilePicture: ImageView
    private lateinit var editProfilePicture: ImageView
    private lateinit var usernameDisplay: TextView
    private lateinit var nickname: TextView
    private lateinit var fullName: TextView
    private lateinit var gender: TextView
    private var prefix: TextView? = null
    private lateinit var email: TextView
    private lateinit var phone: TextView
    private lateinit var logoutButton: Button
    private val REQUEST_SETTINGS_UPDATE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        auth = FirebaseAuth.getInstance()

        profilePicture = findViewById(R.id.profile_picture)
        editProfilePicture = findViewById(R.id.edit_profile_picture)
        usernameDisplay = findViewById(R.id.username_display)
        nickname = findViewById(R.id.nickname)
        fullName = findViewById(R.id.full_name)
        gender = findViewById(R.id.gender)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        logoutButton = findViewById(R.id.logout_button)

        editProfilePicture.setOnClickListener {
            if (auth.currentUser == null) {
                redirectToLogin()
            } else {
                startActivity(Intent(this, ChangeSettingsActivity::class.java))
            }
        }
        loadUserData()

        logoutButton.setOnClickListener {
            logoutButton.text = "Logging out..."
            auth.signOut()

            val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            val savedProfilePicture = File(filesDir, "profile_picture.jpg")
            if (savedProfilePicture.exists()) {
                savedProfilePicture.delete()
            }

            val intent = Intent(this, BranchOverview::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


        if (auth.currentUser == null) {
            logoutButton.text = "LOG IN"
            logoutButton.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        } else {
            logoutButton.text = "LOG OUT"
        }

        val changeSensitiveInfoButton = findViewById<Button>(R.id.change_sensitive_info_button)

        changeSensitiveInfoButton.setOnClickListener {
            if (auth.currentUser == null) {
                redirectToLogin()
            } else {
                startActivity(Intent(this, ChangeSensitiveInformationActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SETTINGS_UPDATE && resultCode == Activity.RESULT_OK) {
            loadUserData()
        }
    }

    private fun redirectToLogin() {
        Toast.makeText(this, "Please log in to edit your profile.", Toast.LENGTH_SHORT).show()
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
    }

    private fun loadUserData() {
        val user = auth.currentUser ?: return

        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)

        var savedNickname = sharedPreferences.getString("nickname", "") ?: ""
        var savedFullName = sharedPreferences.getString("full_name", user?.displayName ?: "") ?: ""
        var savedGender = sharedPreferences.getString("gender", "Other") ?: "Other"
        var savedPrefix = sharedPreferences.getString("prefix", "None") ?: "None"
        var savedPhone = sharedPreferences.getString("phone", "") ?: ""
        val savedCountryCode = sharedPreferences.getString("country_code", "+60") ?: "+60"
        val savedEmail = sharedPreferences.getString("email", "") ?: ""
        var savedProfilePicturePath = sharedPreferences.getString("profilePicturePath", "") ?: ""

        val userDao = UserDatabase.getDatabase(this).userDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val localUser = userDao.getUserByEmail(user.email ?: "")

            withContext(Dispatchers.Main) {
                if (localUser != null) {
                    savedNickname = localUser.nickname.ifEmpty { savedNickname }
                    savedProfilePicturePath = localUser.profilePicturePath.ifEmpty { savedProfilePicturePath }
                    savedPrefix = localUser.prefix.ifEmpty { savedPrefix }
                    savedGender = localUser.gender.ifEmpty { savedGender }

                    sharedPreferences.edit()
                        .putString("nickname", savedNickname)
                        .putString("profilePicturePath", savedProfilePicturePath)
                        .putString("prefix", savedPrefix)
                        .putString("gender", savedGender)
                        .apply()
                }

                nickname.text = savedNickname.ifEmpty { "Not set" }
                fullName.text = if (savedFullName.isNotEmpty()) savedFullName else "Not set"
                gender.text = savedGender
                prefix?.text = if (savedPrefix == "None") "" else savedPrefix

                val displayName = savedNickname.ifEmpty { savedFullName.ifEmpty { "Guest User" } }
                usernameDisplay.text = if (user != null) {
                    if (savedPrefix == "None") "Hello, $displayName" else "Hello, $savedPrefix $displayName"
                } else {
                    "Hello, Guest User"
                }
                updateProfilePicture(savedProfilePicturePath, savedGender)
            }

            withContext(Dispatchers.Main) {
                if (localUser != null) {
                    phone.text = localUser.phone.ifEmpty { "Not provided" }
                } else {
                    phone.text = "Not provided"
                }
            }
        }

        if (savedPrefix.isEmpty() || savedPrefix == "Other") {
            savedPrefix = when (savedGender) {
                "Male" -> "Mr."
                "Female" -> "Ms."
                else -> "None"
            }
            sharedPreferences.edit().putString("prefix", savedPrefix).apply()
        }

        savedPhone = savedPhone.replace("+XXX", "").trim()

        if (savedPhone.startsWith("+")) {
            val phoneWithoutCode = savedPhone.substringAfter(" ")
            savedPhone = if (!phoneWithoutCode.startsWith(savedCountryCode)) {
                "$savedCountryCode $phoneWithoutCode"
            } else {
                savedPhone
            }
        }

        phone.text = savedPhone.ifEmpty { "Not provided" }
        email.text = savedEmail.ifEmpty { "Not provided" }
    }

    private fun updateProfilePicture(savedProfilePicturePath: String, savedGender: String) {
        if (savedProfilePicturePath.isNotEmpty() && File(savedProfilePicturePath).exists()) {
            profilePicture.setImageURI(Uri.fromFile(File(savedProfilePicturePath)))
        } else {
            val defaultProfilePicture = when (savedGender) {
                "Male" -> R.drawable.default_male
                "Female" -> R.drawable.default_female
                else -> R.drawable.default_other
            }
            profilePicture.setImageResource(defaultProfilePicture)
        }
    }
}
