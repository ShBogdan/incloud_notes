package com.incloudlogic.taskmanager.data.network

import com.incloudlogic.taskmanager.data.dto.LoginRequestDto
import com.incloudlogic.taskmanager.data.dto.LoginResponseDto
import com.incloudlogic.taskmanager.data.dto.RefreshTokenRequestDto
import com.incloudlogic.taskmanager.data.dto.TaskDto
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API service interface for task management
 */
interface ApiService {
    
    // Authentication endpoints
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): Response<LoginResponseDto>
    
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
    
    // Task endpoints
    @GET("tasks")
    suspend fun getTasks(): Response<List<TaskDto>>
    
    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") taskId: String): Response<TaskDto>
    
    @POST("tasks")
    suspend fun createTask(@Body task: TaskDto): Response<TaskDto>
    
    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") taskId: String, @Body task: TaskDto): Response<TaskDto>
    
    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") taskId: String): Response<Unit>
}
