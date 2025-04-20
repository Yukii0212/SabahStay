package com.example.testversion

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.UserDatabase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ChangeSettingsActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_PICK = 1001

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var profilePicture: ImageView
    private lateinit var nickname: EditText
    private lateinit var fullName: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var genderMale: RadioButton
    private lateinit var genderFemale: RadioButton
    private lateinit var genderOther: RadioButton
    private lateinit var prefixSpinner: Spinner
    private lateinit var user: FirebaseUser
    private lateinit var discardButton: Button
    private lateinit var confirmButton: Button
    private var profilePicturePath: String = ""
    private var originalProfilePicturePath: String = ""
    private var userManuallyChangedPrefix = false
    private val REQUEST_IMAGE_CROP = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_settings)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        user = FirebaseAuth.getInstance().currentUser!!

        // Initialize UI elements
        profilePicture = findViewById(R.id.profile_picture)
        nickname = findViewById(R.id.nickname)
        fullName = findViewById(R.id.fullName)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        genderMale = findViewById(R.id.genderMale)
        genderFemale = findViewById(R.id.genderFemale)
        genderOther = findViewById(R.id.genderOther)
        prefixSpinner = findViewById(R.id.prefixSpinner)
        discardButton = findViewById(R.id.discardChangeButton)
        confirmButton = findViewById(R.id.confirmChangeButton)

        val changeProfilePictureButton = findViewById<Button>(R.id.changeProfilePictureButton)
        val savedProfilePicturePath = sharedPreferences.getString("profilePicturePath", "")

        changeProfilePictureButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        if (!savedProfilePicturePath.isNullOrEmpty()) {
            profilePicture.setImageURI(Uri.parse(savedProfilePicturePath))
        }

        val updatedProfilePicturePath = intent.getStringExtra("profilePicturePath")
        if (!updatedProfilePicturePath.isNullOrEmpty()) {
            profilePicture.setImageURI(Uri.parse(updatedProfilePicturePath))
        }

        // Ensure prefix updates automatically when gender is changed, but respects manual selection
        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val manuallyChanged = sharedPreferences.getBoolean("manualPrefixSet", false)

            if (!manuallyChanged) {
                val newPrefix = when (checkedId) {
                    R.id.genderMale -> "Mr."
                    R.id.genderFemale -> "Ms."
                    else -> "None"
                }
                updatePrefixSelection(newPrefix)
            }
        }

        // Set listener to detect manual prefix selection
        prefixSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (prefixSpinner.isPressed || prefixSpinner.hasFocus()) {
                    userManuallyChangedPrefix = true
                    sharedPreferences.edit().putBoolean("manualPrefixSet", true).apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        loadUserData() //Load user data

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
        var savedProfilePicturePath = sharedPreferences.getString("profilePicturePath", "") ?: ""

        //Restore manual prefix selection flag
        userManuallyChangedPrefix = sharedPreferences.getBoolean("manualPrefixSet", false)

        //Store the original profile picture path before allowing changes
        originalProfilePicturePath = savedProfilePicturePath
        profilePicturePath = savedProfilePicturePath

        //Load user details from Room Database & update UI in real-time
        val userDao = UserDatabase.getDatabase(this).userDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val localUser = userDao.getUserByEmail(user.email ?: "")

            if (localUser != null) {
                withContext(Dispatchers.Main) {
                    //Load values from Room Database
                    savedNickname = localUser.nickname.ifEmpty { savedNickname }
                    savedProfilePicturePath = localUser.profilePicturePath.ifEmpty { savedProfilePicturePath }
                    savedPrefix = localUser.prefix.ifEmpty { savedPrefix }
                    savedGender = localUser.gender.ifEmpty { savedGender }

                    //Save updated values to SharedPreferences for consistency
                    sharedPreferences.edit()
                        .putString("nickname", savedNickname)
                        .putString("profilePicturePath", savedProfilePicturePath)
                        .putString("prefix", savedPrefix)
                        .putString("gender", savedGender)
                        .apply()

                    //Update UI elements immediately
                    nickname.setText(savedNickname)
                    fullName.setText(savedFullName)
                }
            }
        }

        // Load the current profile picture
        if (savedProfilePicturePath.isNotEmpty() && File(savedProfilePicturePath).exists()) {
            profilePicture.setImageURI(Uri.fromFile(File(savedProfilePicturePath)))
        } else {
            profilePicture.setImageResource(R.drawable.default_male) // Set default image
        }

        // Load and Pre-Select Gender from SharedPreferences
        when (savedGender) {
            "Male" -> genderRadioGroup.check(R.id.genderMale)
            "Female" -> genderRadioGroup.check(R.id.genderFemale)
            else -> genderRadioGroup.check(R.id.genderOther)
        }

        // Only update prefix if the user has NOT manually changed it
        if (!userManuallyChangedPrefix) {
            savedPrefix = when (savedGender) {
                "Male" -> "Mr."
                "Female" -> "Ms."
                else -> "None"
            }
        }

        updatePrefixSelection(savedPrefix)

        setupPrefixSpinner(savedPrefix)
    }

    private fun saveChanges() {
        val editor = sharedPreferences.edit()

        val newNickname = nickname.text.toString().trim()
        val newFullName = fullName.text.toString().trim()

        val newGender = when (genderRadioGroup.checkedRadioButtonId) {
            R.id.genderMale -> "Male"
            R.id.genderFemale -> "Female"
            else -> "Other"
        }

        val newPrefix = prefixSpinner.selectedItem.toString()

        val manuallyChanged = sharedPreferences.getBoolean("manualPrefixSet", false)
        if (!manuallyChanged) {
            sharedPreferences.edit().putBoolean("manualPrefixSet", true).apply()
        }

        val userDao = UserDatabase.getDatabase(this).userDao()
        lifecycleScope.launch(Dispatchers.IO) {
            userDao.updateNickname(user.email ?: "", newNickname)
            userDao.updateProfilePicture(user.email ?: "", profilePicturePath)
            userDao.updateGender(user.email ?: "", newGender)
            userDao.updatePrefix(user.email ?: "", newPrefix)

            withContext(Dispatchers.Main) {
                editor.putString("nickname", newNickname)
                editor.putString("full_name", newFullName)
                editor.putString("profilePicturePath", profilePicturePath)
                editor.putString("gender", newGender)
                editor.putString("prefix", newPrefix)
                editor.putBoolean("manualPrefixSet", true)
                editor.apply()

                //Send result back to UserProfileActivity to trigger refresh
                val resultIntent = Intent()
                resultIntent.putExtra("profileUpdated", true)
                setResult(Activity.RESULT_OK, resultIntent)

                Toast.makeText(this@ChangeSettingsActivity, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("profileUpdated", true)
        setResult(Activity.RESULT_OK, resultIntent)
        super.onBackPressed()
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
                showCropDialog(imageUri) //Ask if user wants to crop or not
            }
        } else if (requestCode == REQUEST_IMAGE_CROP && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            val croppedBitmap = extras?.getParcelable<Bitmap>("data")

            if (croppedBitmap != null) {
                applyProfilePicture(croppedBitmap) //Apply before navigation
            } else {
                val croppedImageUri = data?.data
                if (croppedImageUri != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, croppedImageUri)
                    applyProfilePicture(bitmap)
                }
            }
        }
    }

    private fun showCropDialog(imageUri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Edit Profile Picture")
            .setMessage("Would you like to crop your profile picture before applying it?")
            .setPositiveButton("Crop") { _, _ ->
                startCrop(imageUri) //User chooses to crop
            }
            .setNegativeButton("Use as-is") { _, _ ->
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                applyProfilePicture(bitmap) //Use without cropping
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun applyProfilePicture(bitmap: Bitmap) {
        runOnUiThread {
            profilePicture.setImageBitmap(bitmap)
        }

        //Save cropped image to internal storage
        val imagePath = saveCroppedImageToLocal(bitmap)
        profilePicturePath = imagePath

        //Save path persistently
        sharedPreferences.edit().putString("profilePicturePath", imagePath).apply()

        Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()

        //Ensure the result is set before redirection
        val resultIntent = Intent()
        resultIntent.putExtra("profilePicturePath", imagePath)
        setResult(Activity.RESULT_OK, resultIntent)

        finish() //Redirect AFTER saving
    }

    private fun startCrop(imageUri: Uri) {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setDataAndType(imageUri, "image/*")
        cropIntent.putExtra("crop", "true")
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        cropIntent.putExtra("outputX", 500)
        cropIntent.putExtra("outputY", 500)
        cropIntent.putExtra("scale", true)
        cropIntent.putExtra("return-data", true)
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP)
    }


    private fun saveCroppedImageToLocal(bitmap: Bitmap): String {
        val directory = File(filesDir, "profile_pictures")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, "profile_pic_${System.currentTimeMillis()}.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun updatePrefixSelection(newPrefix: String) {
        val prefixOptions = arrayOf("Mr.", "Ms.", "Mrs.", "Dr.", "Prof.", "None")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prefixOptions)
        prefixSpinner.adapter = adapter

        val newPosition = prefixOptions.indexOf(newPrefix)
        if (prefixSpinner.selectedItemPosition != newPosition) {
            prefixSpinner.setSelection(newPosition, false) //Prevent unwanted triggers
        }
    }

    private fun setupPrefixSpinner(defaultPrefix: String) {
        val prefixOptions = arrayOf("Mr.", "Ms.", "Mrs.", "Dr.", "Prof.", "None")

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, prefixOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(Color.WHITE) //Ensure selected item is always white
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(Color.WHITE) //Ensure dropdown items are white
                return view
            }
        }

        prefixSpinner.adapter = adapter
        prefixSpinner.setSelection(prefixOptions.indexOf(defaultPrefix), false)
    }
}
