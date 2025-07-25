package com.incloudlogic.taskmanager.data.local

import android.content.Context
import android.content.SharedPreferences
import com.incloudlogic.taskmanager.domain.entity.User

class SharedPreferencesManager(context: Context) {
    private val SP_EMAIL = "spEmail"
    private val SP_PASSWORD = "spPassword"
    private val SP_URL = "spUrl"
    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences("LoggedIn", Context.MODE_PRIVATE)
    private val spEditor = sharedPreference.edit()

    fun saveCredentialsToPrefs(mail: String, password: String, url: String) {
        spEditor.putString(SP_EMAIL, mail)
        spEditor.putString(SP_PASSWORD, password)
        spEditor.putString(SP_URL, url)
        spEditor.apply()
    }

    fun getUser(): User? {
        val email = sharedPreference.getString(SP_EMAIL, null)
        val password = sharedPreference.getString(SP_PASSWORD, null)
        val url = sharedPreference.getString(SP_URL, null)

        return if (email != null && password != null && url != null) {
            User(email, password, url)
        } else {
            null
        }
    }


    fun clearSession() {
        spEditor.clear()
        spEditor.apply()
    }

}