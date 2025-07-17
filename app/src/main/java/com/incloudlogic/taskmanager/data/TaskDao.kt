package com.incloudlogic.taskmanager.data

import android.content.ContentValues
import android.content.Context
import com.incloudlogic.taskmanager.model.Task
import java.util.UUID

class TaskDao(context: Context) {

    private val dbHelper = TasksDatabaseHelper(context)

    fun insert(task: Task) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", task.id.toString())
            put("title", task.title)
            put("content", task.content)
            put("priority", task.priority)
            put("state", if (task.state) 1 else 0)
        }
        db.insert(TasksDatabaseHelper.TABLE_NAME, null, values)
    }

    fun update(task: Task) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", task.title)
            put("content", task.content)
            put("priority", task.priority)
            put("state", if (task.state) 1 else 0)
        }
        db.update(
            TasksDatabaseHelper.TABLE_NAME,
            values,
            "id = ?",
            arrayOf(task.id.toString())
        )
    }

    fun delete(taskId: UUID) {
        val db = dbHelper.writableDatabase
        db.delete(
            TasksDatabaseHelper.TABLE_NAME,
            "id = ?",
            arrayOf(taskId.toString())
        )
    }

    fun getAll(): List<Task> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TasksDatabaseHelper.TABLE_NAME,
            arrayOf("id", "title", "content", "priority", "state"),
            null, null, null, null, null
        )

        val tasks = mutableListOf<Task>()
        with(cursor) {
            while (moveToNext()) {
                val id = UUID.fromString(getString(getColumnIndexOrThrow("id")))
                val title = getString(getColumnIndexOrThrow("title"))
                val content = getString(getColumnIndexOrThrow("content"))
                val priority = getInt(getColumnIndexOrThrow("priority"))
                val state = getInt(getColumnIndexOrThrow("state")) != 0

                tasks.add(Task(id, title, content, priority, state))
            }
            close()
        }
        return tasks
    }

    fun close() {
        dbHelper.close()
    }

}
