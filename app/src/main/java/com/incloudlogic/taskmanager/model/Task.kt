package com.incloudlogic.taskmanager.model

import java.util.UUID

data class Task(
    val id: UUID,
    val title: String,
    val content: String,
    val priority: Int,
    val state: Boolean
)
