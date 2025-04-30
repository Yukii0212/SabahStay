package com.example.testversion

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.UserDatabase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.hbb20.CountryCodePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChangeSensitiveInformationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var user: FirebaseUser

    private lateinit var phoneInput: EditText
    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmNewPassword: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var passwordRequirementsText: TextView
    private lateinit var passwordRequirementsBox: LinearLayout
    private lateinit var showPasswordCheckBox: CheckBox
    private lateinit var countryCodePicker: CountryCodePicker

    private var originalPhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_sensitive_information)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth.currentUser!!

        phoneInput = findViewById(R.id.phoneInput)
        countryCodePicker = findViewById(R.id.countryCodePicker)
        currentPassword = findViewById(R.id.currentPassword)
        newPassword = findViewById(R.id.newPassword)
        confirmNewPassword = findViewById(R.id.confirmNewPassword)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        passwordRequirementsBox = findViewById(R.id.passwordRequirementsBox)
        passwordRequirementsText = findViewById(R.id.passwordRequirementsText)
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox)

        val scrollView = findViewById<ScrollView>(R.id.scrollView)

        phoneInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setupKeyboardScrolling(scrollView, phoneInput)
            }
        }

        countryCodePicker.setOnCountryChangeListener {
            phoneInput.hint = "Enter your phone"
        }

        loadUserData()

        saveChangesButton.setOnClickListener { validateAndSaveChanges() }

        val passwordTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updatePasswordRequirements(
                    newPassword.text.toString(),
                    confirmNewPassword.text.toString()
                )
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        newPassword.addTextChangedListener(passwordTextWatcher)
        confirmNewPassword.addTextChangedListener(passwordTextWatcher)

        newPassword.setOnFocusChangeListener { _, hasFocus ->
            passwordRequirementsBox.visibility = if (hasFocus || confirmNewPassword.hasFocus()) View.VISIBLE else View.GONE
            showPasswordCheckBox.visibility = passwordRequirementsBox.visibility
            if (hasFocus) setupKeyboardScrolling(findViewById(R.id.scrollView), newPassword)
        }
        confirmNewPassword.setOnFocusChangeListener { _, hasFocus ->
            passwordRequirementsBox.visibility = if (hasFocus || newPassword.hasFocus()) View.VISIBLE else View.GONE
            showPasswordCheckBox.visibility = passwordRequirementsBox.visibility
            if (hasFocus) setupKeyboardScrolling(findViewById(R.id.scrollView), confirmNewPassword)
        }

        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val inputType = if (isChecked) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            val typeface = newPassword.typeface

            currentPassword.inputType = inputType
            newPassword.inputType = inputType
            confirmNewPassword.inputType = inputType
            currentPassword.typeface = typeface
            newPassword.typeface = typeface
            confirmNewPassword.typeface = typeface
            currentPassword.setSelection(currentPassword.text.length)
            newPassword.setSelection(newPassword.text.length)
            confirmNewPassword.setSelection(confirmNewPassword.text.length)
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = UserDatabase.getDatabase(this@ChangeSensitiveInformationActivity).userDao()
            val localUser = userDao.getUserByEmail(user.email ?: "")

            withContext(Dispatchers.Main) {
                if (localUser != null) {
                    val fullPhone = localUser.phone
                    originalPhone = fullPhone

                    //Extract the country code and phone number separately
                    val countryCode = fullPhone.takeWhile { it.isDigit() || it == '+' }
                    val phoneNumberWithoutCode = fullPhone.removePrefix(countryCode).trim()

                    //Set country code in CountryCodePicker
                    countryCodePicker.setCountryForPhoneCode(countryCode.replace("+", "").toIntOrNull() ?: 60)

                    //Set only the phone number in EditText
                    phoneInput.setText(phoneNumberWithoutCode)
                }
            }
        }
    }

    private fun setupKeyboardScrolling(scrollView: ScrollView, focusedView: EditText) {
        scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            scrollView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = scrollView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                val scrollToY = when (focusedView.id) {
                    R.id.newPassword, R.id.confirmNewPassword -> focusedView.bottom - scrollView.height / 2
                    else -> focusedView.bottom - (scrollView.height * 2 / 3)
                }
                scrollView.post {
                    scrollView.smoothScrollTo(0, scrollToY.coerceAtLeast(0))
                }
            }
        }
    }

    private fun validateAndSaveChanges() {
        val newPhone = phoneInput.text.toString().trim()
        val oldPassword = currentPassword.text.toString().trim()
        val newPass = newPassword.text.toString().trim()
        val confirmPass = confirmNewPassword.text.toString().trim()

        //No changes made
        if (newPhone == originalPhone && newPass.isEmpty()) {
            Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show()
            return
        }

        //Require current password ONLY IF changes are being made
        if ((newPhone != originalPhone || newPass.isNotEmpty()) && oldPassword.isEmpty()) {
            Toast.makeText(this, "Current password is required to make changes.", Toast.LENGTH_SHORT).show()
            return
        }

        //Ask the user to enter a new password if they made any changes
        if (newPass.isNotEmpty() || confirmPass.isNotEmpty()) {
            if (!validatePassword(newPass)) {
                Toast.makeText(this, "New password does not meet requirements.", Toast.LENGTH_SHORT).show()
                return
            }
            if (newPass != confirmPass) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        reauthenticateUser(oldPassword, newPhone, newPass)
    }

    private fun reauthenticateUser(password: String, newPhone: String, newPassword: String) {
        val credential = EmailAuthProvider.getCredential(user.email!!, password)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                user.reauthenticate(credential).await()

                withContext(Dispatchers.Main) {
                    updateUserDetails(newPhone, newPassword)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChangeSensitiveInformationActivity, "Incorrect password.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun updateUserDetails(newPhone: String, newPassword: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = UserDatabase.getDatabase(this@ChangeSensitiveInformationActivity).userDao()

            //Ensure phone number is saved with the country code
            val fullPhone = "+" + countryCodePicker.selectedCountryCode + " " + newPhone.trim()

            //Update phone number in Room Database
            if (newPhone != originalPhone) {
                userDao.updatePhone(user.email!!, fullPhone)
                val updatedUser = userDao.getUserByEmail(user.email!!)

                val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
                sharedPreferences.edit().putString("phone", fullPhone).apply()

                withContext(Dispatchers.Main) {
                    if (updatedUser != null) {
                        Toast.makeText(this@ChangeSensitiveInformationActivity, "Phone number updated successfully!", Toast.LENGTH_SHORT).show()
                        originalPhone = fullPhone
                        finish()
                    } else {
                        Toast.makeText(this@ChangeSensitiveInformationActivity, "Failed to update phone number.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            //Update password
            if (newPassword.isNotEmpty()) {
                user.updatePassword(newPassword).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@ChangeSensitiveInformationActivity, "Password updated successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ChangeSensitiveInformationActivity, "Password update failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            withContext(Dispatchers.Main) {
                val resultIntent = Intent()
                resultIntent.putExtra("profileUpdated", true)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun updatePasswordRequirements(password: String, confirmPassword: String) {
        val isPasswordEmpty = password.isEmpty()
        val isConfirmPasswordEmpty = confirmPassword.isEmpty()

        val requirements = listOf(
            Pair("6 Characters long", password.length >= 6),
            Pair("1 Uppercase letter", password.any { it.isUpperCase() }),
            Pair("1 Lowercase letter", password.any { it.isLowerCase() }),
            Pair("1 Number", password.any { it.isDigit() }),
            Pair(
                if (password == confirmPassword && !isPasswordEmpty && !isConfirmPasswordEmpty)
                    "Passwords match"
                else
                    "Passwords do NOT match",
                password == confirmPassword && !isPasswordEmpty && !isConfirmPasswordEmpty
            )
        )

        passwordRequirementsText.text = requirements.joinToString("\n") { (req, met) ->
            if (met) "✅ $req" else "❌ $req"
        }
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 6 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() }
    }
}
