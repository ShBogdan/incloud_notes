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
import com.incloudlogic.taskmanager.data.local.TaskDao
import com.incloudlogic.taskmanager.domain.entity.Task
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils
import java.util.UUID

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskTitle: Spinner
    private lateinit var taskPriority: Spinner
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
        taskPriority = findViewById(R.id.taskPriority)
        taskContent = findViewById(R.id.taskContent)
        createButton = findViewById(R.id.createButton)
        
        taskPriority.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val priorityText = taskPriority.selectedItem.toString()
                val colorRes = when (priorityText) {
                    "Normal" -> R.color.priority_normal
                    "Major" -> R.color.priority_major
                    "Critical" -> R.color.priority_critical
                    else -> R.color.priority_normal
                }
                (view as? android.widget.TextView)?.setTextColor(getColor(colorRes))
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupCreateTaskButton() {
        val isEditMode = intent.getBooleanExtra("isEditMode", false)
        val taskId = intent.getStringExtra("taskId")?.let { UUID.fromString(it) }
        
        if (isEditMode && taskId != null) {
            // Pre-fill fields for editing
            val title = intent.getStringExtra("taskTitle") ?: ""
            val content = intent.getStringExtra("taskContent") ?: ""
            val priority = intent.getIntExtra("taskPriority", 2)
            
            // Set spinner values
            val titlePosition = getTitlePosition(title)
            val priorityPosition = priority

            taskTitle.setSelection(titlePosition)
            taskPriority.setSelection(priorityPosition)
            taskContent.setText(content)
            
            createButton.text = getString(R.string.update)
        }

        createButton.setOnClickListener {
            val title = taskTitle.selectedItem.toString().trim()
            val priorityText = taskPriority.selectedItem.toString().trim()
            val content = taskContent.text.toString().trim()
            
            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(this, getString(R.string.error_fill_all_fields), LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val priority = when (priorityText) {
                "Normal" -> 2
                "Major" -> 1
                "Critical" -> 0
                else -> 2
            }

            val taskDao = TaskDao(this)
            
            if (isEditMode && taskId != null) {
                // Update existing task
                val existingTask = taskDao.getById(taskId)
                if (existingTask != null) {
                    val updatedTask = existingTask.copy(
                        title = title,
                        content = content,
                        priority = priority
                    )
                    taskDao.update(updatedTask)
                    Toast.makeText(this, getString(R.string.task_updated), LENGTH_SHORT).show()
                }
            } else {
                // Create new task
                val isLocal = intent.getBooleanExtra("isLocal", true)
                taskDao.insert(Task(UUID.randomUUID(), title, content, priority, false, isLocal))
                Toast.makeText(this, "$title ${getString(R.string.task_added)}", LENGTH_SHORT).show()
            }
            
            finish()
        }
    }

    private fun getTitlePosition(title: String): Int {
        val titles = resources.getStringArray(R.array.tasks)
        return titles.indexOf(title).takeIf { it >= 0 } ?: 0
    }

}
