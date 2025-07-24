package com.incloudlogic.taskmanager.data.network

import com.incloudlogic.taskmanager.data.auth.AuthManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to automatically add authentication headers to requests
 */
class AuthInterceptor(private val authManager: AuthManager) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get current authorization header
        val authHeader = authManager.getAuthorizationHeader()
        
        // Add authorization header if available
        val newRequest = if (authHeader != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", authHeader)
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}
