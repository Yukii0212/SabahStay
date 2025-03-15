package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.testversion.database.User
import com.example.testversion.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {
    private var hasAttemptedRegister = false
    private lateinit var nameInput: EditText
    private lateinit var passportInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var registerButton: Button
    private lateinit var passwordRequirementsText: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        // Initialize Views
        nameInput = findViewById(R.id.nameInput)
        passportInput = findViewById(R.id.passportInput)
        phoneInput = findViewById(R.id.phoneInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        registerButton = findViewById(R.id.registerButton)
        passwordRequirementsText = findViewById(R.id.passwordRequirementsText)

        // Set register button to disabled immediately when page loads
        registerButton.isEnabled = false
        registerButton.alpha = 0.5f
        registerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))

        // Add Listeners to Fields
        val inputFields = listOf(nameInput, passportInput, phoneInput, emailInput, passwordInput, confirmPasswordInput)
        inputFields.forEach { it.addTextChangedListener(inputTextWatcher) }
        genderRadioGroup.setOnCheckedChangeListener { _, _ -> updateRegisterButtonState() }

        // Watch password input to update requirements
        passwordInput.addTextChangedListener(passwordTextWatcher)
        confirmPasswordInput.addTextChangedListener(passwordTextWatcher)

        // Show Password Toggle
        val showPasswordCheckBox = findViewById<CheckBox>(R.id.showPasswordCheckBox)
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val inputType = if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            passwordInput.inputType = inputType
            confirmPasswordInput.inputType = inputType

            // Keep cursor at the end after toggling visibility
            passwordInput.setSelection(passwordInput.text.length)
            confirmPasswordInput.setSelection(confirmPasswordInput.text.length)
        }

        // Allows greyed-out button to trigger showInputErrors()
        registerButton.setOnClickListener {
            hasAttemptedRegister = true
            if (validateInputs()) {
                registerUser()
            } else {
                showInputErrors()
            }
        }

        val loginText: TextView = findViewById(R.id.loginText)
        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupKeyboardScrolling()
    }


    // Watcher to Listen to Input Changes
    private val inputTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { updateRegisterButtonState() }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // Watcher for password requirement updates
    private val passwordTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updatePasswordRequirements(
                passwordInput.text.toString(),
                confirmPasswordInput.text.toString(),
                passwordRequirementsText
            )
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // Function to Enable/Disable the Register Button Based on Valid Input
    private fun updateRegisterButtonState() {
        val isValid = validateInputs()

        registerButton.isEnabled = true
        registerButton.alpha = if (isValid) 1.0f else 0.5f
        registerButton.setBackgroundColor(
            ContextCompat.getColor(this, if (isValid) R.color.button_blue else R.color.grey)
        )

        if (hasAttemptedRegister) {
            showInputErrors()
        }

        resetValidInputs()
    }

    private fun showInputErrors() {
        if (!hasAttemptedRegister) return  // Only show errors if user has attempted to register

        val missingFields = mutableListOf<String>()
        val viewsToHighlight = mutableListOf<EditText>()

        if (nameInput.text.toString().trim().isEmpty()) {
            missingFields.add("Name")
            viewsToHighlight.add(nameInput)
        }
        if (passportInput.text.toString().trim().isEmpty()) {
            missingFields.add("Passport")
            viewsToHighlight.add(passportInput)
        }
        if (phoneInput.text.toString().trim().isEmpty()) {
            missingFields.add("Phone")
            viewsToHighlight.add(phoneInput)
        }
        if (emailInput.text.toString().trim().isEmpty() || !isValidEmail(emailInput.text.toString().trim())) {
            missingFields.add("Valid Email")
            viewsToHighlight.add(emailInput)
        }
        if (passwordInput.text.toString().trim().isEmpty() || !isPasswordValid(passwordInput.text.toString().trim())) {
            missingFields.add("Strong Password")
            viewsToHighlight.add(passwordInput)
        }
        if (confirmPasswordInput.text.toString().trim().isEmpty() || confirmPasswordInput.text.toString().trim() != passwordInput.text.toString().trim()) {
            missingFields.add("Matching Passwords")
            viewsToHighlight.add(confirmPasswordInput)
        }
        if (genderRadioGroup.checkedRadioButtonId == -1) {
            missingFields.add("Gender Selection")
            genderRadioGroup.setBackgroundResource(R.drawable.input_error_border) // Highlight gender selection
        } else {
            genderRadioGroup.setBackgroundResource(0) // Reset if valid
        }

        // Show error message in a small popup (Toast)
        if (missingFields.isNotEmpty()) {
            Toast.makeText(this, "Please fix: ${missingFields.joinToString(", ")}", Toast.LENGTH_LONG).show()
        }

        // Highlight missing fields by setting a red border
        viewsToHighlight.forEach { input ->
            input.setBackgroundResource(R.drawable.input_error_border)
        }

        resetValidInputs()

        // Scroll to the first missing field
        if (viewsToHighlight.isNotEmpty()) {
            viewsToHighlight.first().requestFocus()
            (viewsToHighlight.first().parent.parent as ScrollView).smoothScrollTo(0, viewsToHighlight.first().top)
        }

        // Clear the error flag after showing errors so they don’t keep appearing automatically
        hasAttemptedRegister = false
    }

    private fun resetValidInputs() {
        val fieldsToCheck = mapOf(
            nameInput to nameInput.text.toString().trim().isNotEmpty(),
            passportInput to passportInput.text.toString().trim().isNotEmpty(),
            phoneInput to phoneInput.text.toString().trim().isNotEmpty(),
            emailInput to isValidEmail(emailInput.text.toString().trim()),
            passwordInput to isPasswordValid(passwordInput.text.toString().trim()),
            confirmPasswordInput to (confirmPasswordInput.text.toString().trim() == passwordInput.text.toString().trim() && confirmPasswordInput.text.toString().trim().isNotEmpty())
        )

        fieldsToCheck.forEach { (field, isValid) ->
            if (isValid) field.setBackgroundResource(R.drawable.input_default_border)
        }

        if (genderRadioGroup.checkedRadioButtonId != -1) {
            genderRadioGroup.setBackgroundResource(0) // ✅ Reset only if valid
        } else if (hasAttemptedRegister) {
            genderRadioGroup.setBackgroundResource(R.drawable.input_error_border) // ❌ Keep red if still invalid
        }

    }

    // Validate All Input Fields
    private fun validateInputs(): Boolean {
        val name = nameInput.text.toString().trim()
        val passport = passportInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val email = emailInput.text.toString().trim().lowercase()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        val selectedGenderId = genderRadioGroup.checkedRadioButtonId
        val isGenderSelected = selectedGenderId != -1

        val isValid = name.isNotEmpty() &&
                passport.isNotEmpty() &&
                phone.isNotEmpty() &&
                isGenderSelected &&
                isValidEmail(email) &&
                isPasswordValid(password) &&
                password == confirmPassword

        if (hasAttemptedRegister) {
            showInputErrors()
        }

        return isValid
    }

    private fun setupKeyboardScrolling() {
        val scrollView = findViewById<ScrollView>(R.id.scrollView)

        val editTextList = listOf(nameInput, passportInput, phoneInput, emailInput, passwordInput, confirmPasswordInput)

        editTextList.forEach { editText ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    scrollView.postDelayed({
                        scrollView.smoothScrollTo(0, editText.bottom + 200)
                    }, 200)
                }
            }
        }
    }

    // Validate Email Format
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validate Password Rules
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() }
    }

    // Updates Password Requirements dynamically ✅/❌
    private fun updatePasswordRequirements(password: String, confirmPassword: String, textView: TextView) {
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

        textView.text = requirements.joinToString("\n") { (req, met) ->
            if (met) "✅ $req" else "❌ $req"
        }
    }

    // Registration Logic
    private fun registerUser() {
        if (!validateInputs()) {
            Toast.makeText(this, "Please provide valid details", Toast.LENGTH_SHORT).show()
            return
        }

        val name = nameInput.text.toString().trim()
        val passport = passportInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val email = emailInput.text.toString().trim().lowercase()
        val password = passwordInput.text.toString().trim()
        val selectedGenderId = genderRadioGroup.checkedRadioButtonId
        val gender = findViewById<RadioButton>(selectedGenderId).text.toString().lowercase()

        // Firebase Authentication - Create User
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Send Email Verification
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            // Redirect to Email Verification Page
                            val intent = Intent(this, EmailVerificationActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Firebase registration failed
                    Toast.makeText(this, "Firebase Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}