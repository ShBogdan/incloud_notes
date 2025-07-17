package com.incloudlogic.taskmanager.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.model.Task
import java.util.Collections
import java.util.UUID

class CustomAdapter(
    private val dataSet: MutableList<Task>,
    private val context: Context,
    private val listener: OnTaskStateClickListener
) :
    RecyclerView.Adapter<CustomAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var taskId: UUID? = null //для поиска в базе
        val priority: ImageView = view.findViewById(R.id.priority)
        val state: ImageView = view.findViewById(R.id.state)
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_task, viewGroup, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(taskViewHolder: TaskViewHolder, position: Int) {
        taskViewHolder.taskId = dataSet[position].id
        when (dataSet[position].priority) {
            0 -> taskViewHolder.priority.setImageResource(R.drawable.priority_critical_24)
            1 -> taskViewHolder.priority.setImageResource(R.drawable.priority_major_24)
            3 -> taskViewHolder.priority.setImageResource(R.drawable.priority_normal_24)
        }

        when (dataSet[position].state) {
            false -> taskViewHolder.state.setImageResource(R.drawable.check_box_blank_24)
            else -> taskViewHolder.state.setImageResource(R.drawable.check_box_24)
        }
        taskViewHolder.title.text = dataSet[position].title
        taskViewHolder.content.text = dataSet[position].content

        taskViewHolder.state.setOnClickListener {
            val currentState = dataSet[position].state
            if (!currentState) {
                taskViewHolder.state.setImageResource(R.drawable.check_box_24)
                dataSet[position] = dataSet[position].copy(state = true)

            } else {
                taskViewHolder.state.setImageResource(R.drawable.check_box_blank_24)
                dataSet[position] = dataSet[position].copy(state = false)
            }
            listener.onTaskStateClick(dataSet[position], position)
        }


        taskViewHolder.itemView.setOnClickListener {
            Log.d("TaskItemClick", "Clicked on item")
        }


    }

    override fun getItemCount() = dataSet.size

    private fun update(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return

        Collections.swap(dataSet, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun updateData(newData: List<Task>) {
        dataSet.clear()
        dataSet.addAll(newData)
        notifyDataSetChanged()
    }

}
