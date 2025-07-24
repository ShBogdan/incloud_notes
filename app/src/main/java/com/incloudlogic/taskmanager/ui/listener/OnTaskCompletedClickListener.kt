package com.incloudlogic.taskmanager.ui.listener

import com.incloudlogic.taskmanager.domain.entity.Task

interface OnTaskCompletedClickListener {
    fun onTaskCompletedClick(task: Task, position: Int)
}
