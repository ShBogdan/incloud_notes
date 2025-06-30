package com.incloudlogic.taskmanager.model

import java.util.UUID

data class Note(
    val id: UUID,
    val title: String,
    val content: String,
    val priority: Int
)
