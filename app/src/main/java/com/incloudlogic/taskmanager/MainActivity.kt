package com.incloudlogic.taskmanager

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.incloudlogic.taskmanager.databinding.ActivityMainBinding
import com.incloudlogic.taskmanager.ui.TasksOverviewActivity
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils
import com.incloudlogic.taskmanager.utils.SharedPreferencesManager


class MainActivity : AppCompatActivity() {

    private lateinit var textMailInput: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var URLInput: TextInputEditText
    private lateinit var offlineMode: CheckBox
    private lateinit var loginButton: MaterialButton
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)

        sharedPreferencesManager = SharedPreferencesManager(this)

        initViews()
        setupValidation()
        setupLoginButton()
    }

    private fun initViews() {
        textMailInput = findViewById(R.id.textMailInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        URLInput = findViewById(R.id.URLInput)
        offlineMode = findViewById(R.id.offlineMode)

        offlineMode.setOnCheckedChangeListener { _, isChecked ->
            emailInput.isEnabled = !isChecked
            passwordInput.isEnabled = !isChecked
            URLInput.isEnabled = !isChecked
        }

        val user = sharedPreferencesManager.getUser()
        user?.let {
            emailInput.text = SpannableStringBuilder(it.email)
            passwordInput.text = SpannableStringBuilder(it.password)
            URLInput.text = SpannableStringBuilder(it.url)
        }
    }

    private fun setupValidation() {
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

    private fun setupLoginButton() {
        loginButton.setOnClickListener {
            val email = extractEmail()
            val password = extractPassword()
            val URL = extractURL()

            if (validateCredentials(email, password, URL)) {
                sharedPreferencesManager.saveCredentialsToPrefs(email, password, URL)
                navigateToTasksOverviewScreen()
            } else {
                showValidationError()
            }

            Log.i("LoginAttempt", "Email: $email, Password length: ${password.length}")
        }
    }

    private fun extractEmail(): String = emailInput.text?.toString()?.trim() ?: ""

    private fun extractPassword(): String = passwordInput.text?.toString()?.trim() ?: ""

    private fun extractURL(): String = URLInput.text?.toString()?.trim() ?: ""

    private fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9._%+-]+@(gmail\\.com|incloudlogic\\.com)$".toRegex()
        return email.matches(regex)
    }

    private fun validateCredentials(email: String, password: String, url: String): Boolean {
        return if (offlineMode.isChecked) {
            true
        } else {
            isValidEmail(email) && password.isNotBlank() && url.isNotBlank()
        }
    }

    private fun navigateToTasksOverviewScreen() {
        val intent = Intent(this, TasksOverviewActivity::class.java)
        startActivity(intent)
    }

    private fun showValidationError() {
        Toast.makeText(this, "All fields must be filled", LENGTH_SHORT).show()
    }
}
