package com.incloudlogic.taskmanager

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.incloudlogic.taskmanager.databinding.ActivityMainBinding
import com.incloudlogic.taskmanager.ui.NotesOverviewActivity
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils
import com.incloudlogic.taskmanager.utils.SharedPreferencesManager



/**
 * MainActivity is the entry point for user authentication in the TaskManagerApp.
 * <p>
 * Handles user login, input validation, and navigation to the notes overview screen.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var textMailInput: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    /**
     * Called when the activity is starting. Initializes views, sets up validation and login logic.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)

        sharedPreferencesManager = SharedPreferencesManager(this)

        initViews()
        setupEmailValidation()
        setupLoginButton()
    }

    /**
     * Initializes all view elements and pre-fills fields if user data exists.
     */
    private fun initViews() {
        textMailInput = findViewById(R.id.textMailInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)

        val user = sharedPreferencesManager.getUser()
        user?.let {
            emailInput.text = SpannableStringBuilder(it.email)
            passwordInput.text = SpannableStringBuilder(it.password)
        }
    }

    /**
     * Sets up focus change listener for email validation.
     */
    private fun setupEmailValidation() {
        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = emailInput.text?.toString()?.trim() ?: ""
                if (!isValidEmail(email)) {
                    textMailInput.error = getString(R.string.email_hint)
                } else {
                    textMailInput.error = null
                }
            }
        }
    }

    /**
     * Sets up the login button click logic.
     */
    private fun setupLoginButton() {
        loginButton.setOnClickListener {
            val email = extractEmail()
            val password = extractPassword()

            if (validateCredentials(email, password)) {
                sharedPreferencesManager.saveCredentialsToPrefs(email, password)
                navigateToNotesOverviewScreen()
            } else {
                showValidationError()
            }

            Log.i("LoginAttempt", "Email: $email, Password length: ${password.length}")
        }
    }

    /**
     * Extracts email from the input field.
     * @return Trimmed email string.
     */
    private fun extractEmail(): String = emailInput.text?.toString()?.trim() ?: ""

    /**
     * Extracts password from the input field.
     * @return Trimmed password string.
     */
    private fun extractPassword(): String = passwordInput.text?.toString()?.trim() ?: ""

    /**
     * Validates email format (must be gmail.com or incloudlogic.com).
     * @param email The email string to validate.
     * @return True if email is valid, false otherwise.
     */
    private fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9._%+-]+@(gmail\\.com|incloudlogic\\.com)$".toRegex()
        return email.matches(regex)
    }

    /**
     * Validates the entered credentials.
     * @param email The email string.
     * @param password The password string.
     * @return True if both email and password are valid, false otherwise.
     */
    private fun validateCredentials(email: String, password: String): Boolean {
        return isValidEmail(email) && password.isNotBlank()
    }

    /**
     * Navigates to the notes overview screen after successful login.
     */
    private fun navigateToNotesOverviewScreen() {
        val intent = Intent(this, NotesOverviewActivity::class.java)
        startActivity(intent)
    }

    /**
     * Shows a validation error (should be improved to display specific messages).
     */
    private fun showValidationError() {
        // TODO: Add error message display (e.g., Toast or TextInputLayout.setError)
    }
}