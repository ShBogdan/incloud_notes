package com.incloudlogic.taskmanager.ui

import android.os.Bundle
import android.widget.EditText
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

    private lateinit var noteTitle: EditText
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
            val title = noteTitle.text.toString()
            val content = noteContent.text.toString()
            if (title.isBlank() && content.isBlank()) {
                Toast.makeText(this, "All fields mus be filled", LENGTH_SHORT).show()
            } else {
                noteTitle.text?.clear()
                noteContent.text?.clear()
                NoteDao(this).insert(Note(UUID.randomUUID(), title, content, 1))
                Toast.makeText(this, "$title added", LENGTH_SHORT).show()
            }
        }
    }

}
