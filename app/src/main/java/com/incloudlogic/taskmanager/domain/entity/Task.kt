package com.incloudlogic.taskmanager.domain.entity

import java.util.UUID

/**
 * Domain entity for Task
 */
data class Task(
    val id: UUID,
    val title: String,
    val content: String,
    val priority: Int,
    val isCompleted: Boolean,
    val isLocal: Boolean,
    val sortOrder: Int = 0
)
