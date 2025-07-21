package com.incloudlogic.taskmanager.utils

import com.incloudlogic.taskmanager.model.Task

interface OnTaskStateClickListener {
    fun onTaskStateClick(task: Task, position: Int)
}
