package com.incloudlogic.taskmanager.data

import android.content.ContentValues
import android.content.Context
import com.incloudlogic.taskmanager.model.Note
import java.util.UUID

class NoteDao(context: Context) {

    private val dbHelper = NotesDatabaseHelper(context)

    fun insert(note: Note) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", note.id.toString())
            put("title", note.title)
            put("content", note.content)
            put("priority", note.priority)
        }
        db.insert(NotesDatabaseHelper.TABLE_NAME, null, values)
    }

    fun update(note: Note) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", note.title)
            put("content", note.content)
            put("priority", note.priority)
        }
        db.update(
            NotesDatabaseHelper.TABLE_NAME,
            values,
            "id = ?",
            arrayOf(note.id.toString())
        )
    }

    fun delete(noteId: UUID) {
        val db = dbHelper.writableDatabase
        db.delete(
            NotesDatabaseHelper.TABLE_NAME,
            "id = ?",
            arrayOf(noteId.toString())
        )
    }

    fun getAll(): List<Note> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NotesDatabaseHelper.TABLE_NAME,
            arrayOf("id", "title", "content", "priority"),
            null, null, null, null, null
        )

        val notes = mutableListOf<Note>()
        with(cursor) {
            while (moveToNext()) {
                val id = UUID.fromString(getString(getColumnIndexOrThrow("id")))
                val title = getString(getColumnIndexOrThrow("title"))
                val content = getString(getColumnIndexOrThrow("content"))
                val priority = getInt(getColumnIndexOrThrow("priority"))

                notes.add(Note(id, title, content, priority))
            }
            close()
        }
        return notes
    }

    fun close() {
        dbHelper.close()
    }

}
