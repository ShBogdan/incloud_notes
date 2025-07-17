package com.incloudlogic.taskmanager.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.data.TaskDao
import com.incloudlogic.taskmanager.model.Task
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils
import java.util.UUID

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskTitle: Spinner
    private lateinit var taskContent: EditText
    private lateinit var createButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)
        initViews()
        setupCreateTaskButton()
    }

    private fun initViews() {
        taskTitle = findViewById(R.id.taskTitle)
        taskContent = findViewById(R.id.taskContent)
        createButton = findViewById(R.id.createButton)
    }

    private fun setupCreateTaskButton() {
        createButton.setOnClickListener {
            val title = taskTitle.selectedItem.toString().trim()
            val content = taskContent.text.toString().trim()
            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(this, getString(R.string.error_fill_all_fields), LENGTH_SHORT).show()
                return@setOnClickListener
            }
            TaskDao(this).insert(Task(UUID.randomUUID(), title, content, 1, false))
            taskContent.text?.clear()
            Toast.makeText(this, "$title ${getString(R.string.task_added)}", LENGTH_SHORT).show()
            finish()
        }
    }
}
