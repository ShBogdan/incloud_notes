package com.incloudlogic.taskmanager.data.local

import android.content.ContentValues
import android.content.Context
import com.incloudlogic.taskmanager.domain.entity.Task
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
            put("is_completed", if (task.isCompleted) 1 else 0)
            put("is_local", if (task.isLocal) 1 else 0)
            put("sort_order", task.sortOrder)
        }
        db.insert(TasksDatabaseHelper.TABLE_NAME, null, values)
    }

    fun update(task: Task) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", task.title)
            put("content", task.content)
            put("priority", task.priority)
            put("is_completed", if (task.isCompleted) 1 else 0)
            put("is_local", if (task.isLocal) 1 else 0)
            put("sort_order", task.sortOrder)
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
            arrayOf("id", "title", "content", "priority", "is_completed", "is_local", "sort_order"),
            null, null, null, null, "sort_order ASC, id ASC"
        )

        val tasks = mutableListOf<Task>()
        with(cursor) {
            while (moveToNext()) {
                val id = UUID.fromString(getString(getColumnIndexOrThrow("id")))
                val title = getString(getColumnIndexOrThrow("title"))
                val content = getString(getColumnIndexOrThrow("content"))
                val priority = getInt(getColumnIndexOrThrow("priority"))
                val isCompleted = getInt(getColumnIndexOrThrow("is_completed")) != 0
                val isLocal = getInt(getColumnIndexOrThrow("is_local")) != 0
                val sortOrder = getInt(getColumnIndexOrThrow("sort_order"))

                tasks.add(Task(id, title, content, priority, isCompleted, isLocal, sortOrder))
            }
            close()
        }
        return tasks
    }

    fun getById(taskId: UUID): Task? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TasksDatabaseHelper.TABLE_NAME,
            arrayOf("id", "title", "content", "priority", "is_completed", "is_local"),
            "id = ?",
            arrayOf(taskId.toString()),
            null, null, null
        )

        var task: Task? = null
        with(cursor) {
            if (moveToFirst()) {
                val id = UUID.fromString(getString(getColumnIndexOrThrow("id")))
                val title = getString(getColumnIndexOrThrow("title"))
                val content = getString(getColumnIndexOrThrow("content"))
                val priority = getInt(getColumnIndexOrThrow("priority"))
                val isCompleted = getInt(getColumnIndexOrThrow("is_completed")) != 0
                val isLocal = getInt(getColumnIndexOrThrow("is_local")) != 0

                task = Task(
                    id,
                    title,
                    content,
                    priority,
                    isCompleted,
                    isLocal
                )
            }
            close()
        }
        return task
    }

    fun updateSortOrders(tasks: List<Task>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            tasks.forEachIndexed { index, task ->
                val values = ContentValues().apply {
                    put("sort_order", index)
                }
                db.update(
                    TasksDatabaseHelper.TABLE_NAME,
                    values,
                    "id = ?",
                    arrayOf(task.id.toString())
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun close() {
        dbHelper.close()
    }

}
