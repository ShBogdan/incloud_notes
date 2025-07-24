package com.incloudlogic.taskmanager.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Objects for Authentication
 */
data class LoginRequestDto(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

data class LoginResponseDto(
    @SerializedName("access_token")
    val accessToken: String,
    
    @SerializedName("refresh_token")
    val refreshToken: String,
    
    @SerializedName("token_type")
    val tokenType: String = "Bearer",
    
    @SerializedName("expires_in")
    val expiresIn: Long,
    
    @SerializedName("user")
    val user: UserDto
)

data class RefreshTokenRequestDto(
    @SerializedName("refresh_token")
    val refreshToken: String
)

data class UserDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("name")
    val name: String? = null
)
