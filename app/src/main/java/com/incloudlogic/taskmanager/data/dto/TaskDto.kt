package com.incloudlogic.taskmanager.data.dto

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Data Transfer Object for Task from REST API
 */
data class TaskDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("priority")
    val priority: Int,
    
    @SerializedName("is_completed")
    val isCompleted: Boolean,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    
    @SerializedName("user_id")
    val userId: String? = null
)
