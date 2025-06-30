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
 * Главная активность для авторизации пользователя.
 */
class MainActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityMainBinding

    private lateinit var textMailInput: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var sharedPreferencesManager: SharedPreferencesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)

        sharedPreferencesManager =  SharedPreferencesManager(this)

        initViews()
        setupEmailValidation()
        setupLoginButton()
    }

    /**
     * Инициализация всех view-элементов.
     */
    private fun initViews() {
        textMailInput = findViewById(R.id.textMailInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)

        val user = sharedPreferencesManager.getUser()
        if(sharedPreferencesManager.getUser()!=null){
            emailInput.text = SpannableStringBuilder(user?.email)
            passwordInput.text = SpannableStringBuilder(user?.password)
        }

    }

    /**
     * Установка слушателя фокуса для проверки email.
     */
    private fun setupEmailValidation() {
       emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = emailInput.text.toString()
                if (!isValidEmail(email)) {
                    textMailInput.error = getString(R.string.email_hint)
                } else {
                    textMailInput.error = null
                }
            }
        }
    }

    /**
     * Установка логики кнопки входа.
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
     * Извлекает email из поля ввода.
     */
    private fun extractEmail(): String {
        return emailInput.text?.toString()?.trim() ?: ""
    }

    /**
     * Извлекает пароль из поля ввода.
     */
    private fun extractPassword(): String {
        return passwordInput.text?.toString()?.trim() ?: ""
    }

    /**
     * Проверяет валидность email (gmail.com или incloudlogic.com).
     */
    private fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9._%+-]+@(gmail\\.com|incloudlogic\\.com)$".toRegex()
        return email.matches(regex)
    }

    /**
     * Проверяет валидность введённых данных.
     */
    private fun validateCredentials(email: String, password: String): Boolean {
        return isValidEmail(email) && password.isNotBlank()
    }

    /**
     * Навигация к экрану задач после успешной авторизации.
     */
    private fun navigateToNotesOverviewScreen() {
        val intent = Intent(this, NotesOverviewActivity::class.java)
        startActivity(intent)
    }

    /**
     * Вывод ошибки валидации (в реальном проекте лучше отображать конкретные ошибки).
     */
    private fun showValidationError() {
        // TODO: добавить отображение сообщений об ошибке (например, через Toast или TextInputLayout.setError)
    }
}