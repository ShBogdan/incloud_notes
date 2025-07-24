package com.incloudlogic.taskmanager.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.domain.entity.Task
import java.util.Collections
import java.util.UUID

class CustomAdapter(
    private val dataSet: MutableList<Task>,
    private val listener: OnTaskCompletedClickListener
) :
    RecyclerView.Adapter<CustomAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var taskId: UUID? = null
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
        val priority: ImageView = view.findViewById(R.id.priority)
        val isCompleted: ImageView = view.findViewById(R.id.isCompleted)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_task, viewGroup, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(taskViewHolder: TaskViewHolder, position: Int) {
        val task = dataSet[position]
        taskViewHolder.taskId = task.id
        
        // Ensure priority image is always properly set
        when (task.priority) {
            0 -> taskViewHolder.priority.setImageResource(R.drawable.priority_critical_24)
            1 -> taskViewHolder.priority.setImageResource(R.drawable.priority_major_24)
            2 -> taskViewHolder.priority.setImageResource(R.drawable.priority_normal_24)
            else -> taskViewHolder.priority.setImageResource(R.drawable.priority_normal_24) // Default fallback
        }

        // Ensure completion state is always properly set
        if (task.isCompleted) {
            taskViewHolder.isCompleted.setImageResource(R.drawable.check_box_24)
        } else {
            taskViewHolder.isCompleted.setImageResource(R.drawable.check_box_blank_24)
        }
        
        taskViewHolder.title.text = task.title
        taskViewHolder.content.text = task.content

        taskViewHolder.isCompleted.setOnClickListener {
            val currentState = dataSet[position].isCompleted
            if (!currentState) {
                taskViewHolder.isCompleted.setImageResource(R.drawable.check_box_24)
                dataSet[position] = dataSet[position].copy(isCompleted = true)
            } else {
                taskViewHolder.isCompleted.setImageResource(R.drawable.check_box_blank_24)
                dataSet[position] = dataSet[position].copy(isCompleted = false)
            }
            listener.onTaskCompletedClick(dataSet[position], position)
        }

        taskViewHolder.itemView.setOnClickListener {
            Log.d("TaskItemClick", "Clicked on item")
        }
    }

    override fun getItemCount() = dataSet.size

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
