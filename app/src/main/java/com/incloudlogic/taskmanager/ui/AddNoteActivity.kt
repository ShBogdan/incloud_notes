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
import com.incloudlogic.taskmanager.data.NoteDao
import com.incloudlogic.taskmanager.model.Note
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils
import java.util.UUID

class AddNoteActivity : AppCompatActivity() {

    private lateinit var noteTitle: Spinner
    private lateinit var noteContent: EditText
    private lateinit var createButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)
        initViews()
        setupCreateNoteButton()
    }

    private fun initViews() {
        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)
        createButton = findViewById(R.id.createButton)
    }

    private fun setupCreateNoteButton() {
        createButton.setOnClickListener {
            val title = noteTitle.selectedItem.toString().trim()
            val content = noteContent.text.toString().trim()
            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(this, getString(R.string.error_fill_all_fields), LENGTH_SHORT).show()
                return@setOnClickListener
            }
            NoteDao(this).insert(Note(UUID.randomUUID(), title, content, 1))
            noteContent.text?.clear()
            Toast.makeText(this, "$title ${getString(R.string.note_added)}", LENGTH_SHORT).show()
            finish()
        }
    }
}
