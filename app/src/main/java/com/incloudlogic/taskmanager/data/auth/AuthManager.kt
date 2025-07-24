package com.incloudlogic.taskmanager.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base class for REST authentication management
 * Handles token storage, authentication state, and token refresh
 */
class AuthManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _currentToken = MutableStateFlow<String?>(null)
    val currentToken: StateFlow<String?> = _currentToken.asStateFlow()
    
    init {
        // Load saved authentication state
        loadAuthState()
    }
    
    /**
     * Save authentication tokens
     */
    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        val expirationTime = System.currentTimeMillis() + (expiresIn * 1000)
        
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_TOKEN_EXPIRATION, expirationTime)
            apply()
        }
        
        _currentToken.value = accessToken
        _isAuthenticated.value = true
    }
    
    /**
     * Get current access token
     */
    fun getAccessToken(): String? {
        val token = prefs.getString(KEY_ACCESS_TOKEN, null)
        val expiration = prefs.getLong(KEY_TOKEN_EXPIRATION, 0L)
        
        // Check if token is expired
        return if (token != null && System.currentTimeMillis() < expiration) {
            token
        } else {
            null
        }
    }
    
    /**
     * Get refresh token
     */
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Check if current token is expired
     */
    fun isTokenExpired(): Boolean {
        val expiration = prefs.getLong(KEY_TOKEN_EXPIRATION, 0L)
        return System.currentTimeMillis() >= expiration
    }
    
    /**
     * Clear all authentication data
     */
    fun clearAuth() {
        prefs.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_EXPIRATION)
            apply()
        }
        
        _currentToken.value = null
        _isAuthenticated.value = false
    }
    
    /**
     * Get authorization header value
     */
    fun getAuthorizationHeader(): String? {
        val token = getAccessToken()
        return if (token != null) "Bearer $token" else null
    }
    
    private fun loadAuthState() {
        val token = getAccessToken()
        _currentToken.value = token
        _isAuthenticated.value = token != null
    }
    
    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRATION = "token_expiration"
    }
}
