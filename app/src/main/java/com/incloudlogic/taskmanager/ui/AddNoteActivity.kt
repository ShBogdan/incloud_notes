package com.incloudlogic.taskmanager.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils

class AddNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)
    }
}