package com.incloudlogic.taskmanager.utils

import com.incloudlogic.taskmanager.model.Task

interface OnTaskCompletedClickListener {
    fun onTaskCompletedClick(task: Task, position: Int)
}
