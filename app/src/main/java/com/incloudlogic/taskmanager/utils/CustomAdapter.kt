package com.incloudlogic.taskmanager.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.model.Note
import java.util.Collections
import java.util.UUID

class CustomAdapter(private val dataSet: MutableList<Note>, private val context: Context) :
    RecyclerView.Adapter<CustomAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var noteId: UUID? = null //для поиска в базе
        val priorityIndicator: ImageView = view.findViewById(R.id.priorityIndicator)
        val noteTitle: TextView = view.findViewById(R.id.noteTitle)
        val noteContent: TextView = view.findViewById(R.id.noteContent)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_note, viewGroup, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(noteViewHolder: NoteViewHolder, position: Int) {
        noteViewHolder.noteId = dataSet[position].id
        when (dataSet[position].priority) {
            0 -> noteViewHolder.priorityIndicator.setImageResource(R.drawable.priority_critical_24)
            1 -> noteViewHolder.priorityIndicator.setImageResource(R.drawable.priority_major_24)
            3 -> noteViewHolder.priorityIndicator.setImageResource(R.drawable.priority_normal_24)
        }
        noteViewHolder.noteTitle.text = dataSet[position].title
        noteViewHolder.noteContent.text = dataSet[position].content
    }

    override fun getItemCount() = dataSet.size

    fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return

        Collections.swap(dataSet, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

}