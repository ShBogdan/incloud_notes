package com.incloudlogic.taskmanager.utils

import com.incloudlogic.taskmanager.domain.entity.Task

interface OnTaskCompletedClickListener {
    fun onTaskCompletedClick(task: Task, position: Int)
}
