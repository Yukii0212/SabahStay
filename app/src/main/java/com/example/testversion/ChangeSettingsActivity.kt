package com.example.testversion

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.UserDatabase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ChangeSettingsActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_PICK = 1001

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var profilePicture: ImageView
    private lateinit var editProfilePicture: ImageView
    private lateinit var usernameDisplay: TextView
    private lateinit var nickname: EditText
    private lateinit var fullName: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var genderMale: RadioButton
    private lateinit var genderFemale: RadioButton
    private lateinit var genderOther: RadioButton
    private lateinit var prefixSpinner: Spinner
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var discardButton: Button
    private lateinit var confirmButton: Button
    private var profilePicturePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_settings)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)

        // Initialize UI elements
        profilePicture = findViewById(R.id.profile_picture)
        nickname = findViewById(R.id.nickname)
        fullName = findViewById(R.id.fullName)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        genderMale = findViewById(R.id.genderMale)
        genderFemale = findViewById(R.id.genderFemale)
        genderOther = findViewById(R.id.genderOther)
        prefixSpinner = findViewById(R.id.prefixSpinner)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        discardButton = findViewById(R.id.discardChangeButton)
        confirmButton = findViewById(R.id.confirmChangeButton)

        val changeProfilePictureButton = findViewById<Button>(R.id.changeProfilePictureButton)

        changeProfilePictureButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }


        loadUserData()

        discardButton.setOnClickListener { finish() }
        confirmButton.setOnClickListener { saveChanges() }
    }

    private fun loadUserData() {
        val user = auth.currentUser ?: return
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)

        var savedNickname = sharedPreferences.getString("nickname", "") ?: ""
        var savedFullName = sharedPreferences.getString("full_name", user.displayName ?: "") ?: ""
        var savedGender = sharedPreferences.getString("gender", "Other") ?: "Other"
        var savedPrefix = sharedPreferences.getString("prefix", "None") ?: "None"
        var savedPhone = sharedPreferences.getString("phone", "") ?: ""
        val savedCountryCode = sharedPreferences.getString("country_code", "+XXX") ?: "+XXX"
        val savedEmail = user.email ?: ""
        var savedProfilePicturePath = sharedPreferences.getString("profilePicturePath", "") ?: ""

        // ✅ Load user details from Room Database & update UI in real-time
        val userDao = UserDatabase.getDatabase(this).userDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val localUser = userDao.getUserByEmail(savedEmail)

            if (localUser != null) {
                withContext(Dispatchers.Main) {
                    // ✅ Load values from Room Database
                    savedNickname = localUser.nickname.ifEmpty { savedNickname }
                    savedProfilePicturePath = localUser.profilePicturePath.ifEmpty { savedProfilePicturePath }
                    savedPrefix = localUser.prefix.ifEmpty { savedPrefix }
                    savedGender = localUser.gender.ifEmpty { savedGender }
                    savedPhone = localUser.phone.ifEmpty { savedPhone }

                    // ✅ Save updated values to SharedPreferences for consistency
                    sharedPreferences.edit()
                        .putString("nickname", savedNickname)
                        .putString("profilePicturePath", savedProfilePicturePath)
                        .putString("prefix", savedPrefix)
                        .putString("gender", savedGender)
                        .putString("phone", savedPhone)
                        .apply()

                    // ✅ Update UI elements immediately
                    nickname.setText(savedNickname)
                    fullName.setText(savedFullName)
                    email.setText(savedEmail)
                }
            }
        }

        // ✅ Ensure correct phone number format (remove extra `+XXX`)
        if (!savedPhone.startsWith(savedCountryCode)) {
            savedPhone = "$savedCountryCode $savedPhone"
        }
        phone.setText(savedPhone)

        // ✅ Load and Pre-Select Gender from SharedPreferences
        when (savedGender) {
            "Male" -> genderRadioGroup.check(R.id.genderMale)
            "Female" -> genderRadioGroup.check(R.id.genderFemale)
            else -> genderRadioGroup.check(R.id.genderOther)
        }

        // ✅ Assign Default Prefix if None Exists
        if (savedPrefix.isEmpty() || savedPrefix == "Other") {
            savedPrefix = when (savedGender) {
                "Male" -> "Mr."
                "Female" -> "Ms."
                else -> "None"
            }
            sharedPreferences.edit().putString("prefix", savedPrefix).apply()
        }

        // ✅ Load Prefix Dropdown Correctly
        val prefixOptions = arrayOf("Mr.", "Ms.", "Mrs.", "Dr.", "Prof.", "None")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prefixOptions)
        prefixSpinner.adapter = adapter
        prefixSpinner.setSelection(prefixOptions.indexOf(savedPrefix))

        prefixSpinner.setBackgroundResource(R.drawable.spinner_background)
    }



    private fun requestPasswordForField(editText: EditText) {
        Toast.makeText(this, "Feature not implemented yet!", Toast.LENGTH_SHORT).show()
    }

    private fun saveChanges() {
        val editor = sharedPreferences.edit()

        val newNickname = nickname.text.toString().trim()
        val newFullName = fullName.text.toString().trim()
        val newPhone = phone.text.toString().trim()

        // ✅ Keep old gender if not changed
        val selectedGenderId = genderRadioGroup.checkedRadioButtonId
        val newGender = when (selectedGenderId) {
            R.id.genderMale -> "Male"
            R.id.genderFemale -> "Female"
            else -> sharedPreferences.getString("gender", "Other") ?: "Other"
        }

        val newEmail = email.text.toString().trim()

        val userDao = UserDatabase.getDatabase(this).userDao()
        lifecycleScope.launch(Dispatchers.IO) {
            userDao.updateNickname(newEmail, newNickname)
            userDao.updateProfilePicture(newEmail, profilePicturePath)
            userDao.updateGender(newEmail, newGender)

            withContext(Dispatchers.Main) {
                // ✅ Save new data to SharedPreferences
                editor.putString("nickname", newNickname)
                editor.putString("full_name", newFullName)
                editor.putString("phone", newPhone)
                editor.putString("gender", newGender) // ✅ Save only if changed
                editor.putString("email", newEmail)
                editor.putString("profilePicturePath", profilePicturePath)
                editor.apply()

                Toast.makeText(this@ChangeSettingsActivity, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showPasswordPrompt(newEmail: String, newPhone: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Confirm Identity")
        dialog.setMessage("Enter your password to update Email or Phone.")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        dialog.setView(input)

        dialog.setPositiveButton("Confirm") { _, _ ->
            val password = input.text.toString().trim()
            if (password.isNotEmpty()) {
                reauthenticateUser(password, newEmail, newPhone)
            } else {
                Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        dialog.show()
    }


    private fun reauthenticateUser(password: String, newEmail: String, newPhone: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", password)

        user?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                applyChanges(sharedPreferences.edit(), newEmail, newPhone)
            } else {
                Toast.makeText(this, "Incorrect password!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyChanges(editor: SharedPreferences.Editor, newEmail: String? = null, newPhone: String? = null) {
        editor.putString("nickname", nickname.text.toString())
        editor.putString("full_name", fullName.text.toString())

        if (!newPhone.isNullOrEmpty()) editor.putString("phone", newPhone)
        if (!newEmail.isNullOrEmpty()) editor.putString("email", newEmail)

        val selectedGender = when {
            genderMale.isChecked -> "Male"
            genderFemale.isChecked -> "Female"
            else -> "Other"
        }
        editor.putString("gender", selectedGender)
        editor.putString("prefix", prefixSpinner.selectedItem.toString())

        editor.apply()
        Toast.makeText(this, "Settings updated successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                profilePicture.setImageURI(imageUri) // ✅ Show updated image instantly

                // ✅ Save the image to local storage
                val imagePath = saveImageToLocal(imageUri)
                profilePicturePath = imagePath  // ✅ Update profilePicturePath variable
                updateProfilePictureInDatabase(imagePath)
            }
        }
    }

    private fun saveImageToLocal(uri: Uri?): String {
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val file = File(filesDir, "profile_picture_${System.currentTimeMillis()}.jpg")

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            return file.absolutePath // Return the stored path
        }
        return ""
    }

    private fun updateProfilePictureInDatabase(imagePath: String) {
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "") ?: return

        val userDao = UserDatabase.getDatabase(this).userDao()
        lifecycleScope.launch(Dispatchers.IO) {
            userDao.updateProfilePicture(email, imagePath)

            sharedPreferences.edit().putString("profilePicturePath", imagePath).apply()
        }
    }
}
