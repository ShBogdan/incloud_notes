package com.incloudlogic.taskmanager.ui.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.domain.entity.Task
import com.incloudlogic.taskmanager.ui.listener.OnTaskCompletedClickListener
import java.util.Collections
import java.util.UUID

class CustomAdapter(
    private val dataSet: MutableList<Task>,
    private val listener: OnTaskCompletedClickListener,
    private val onTaskClickListener: (Task) -> Unit
) :
    RecyclerView.Adapter<CustomAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var taskId: UUID? = null
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
        val priority: View = view.findViewById(R.id.priority)
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

        val background = taskViewHolder.priority.background
        if (background is GradientDrawable) {
            background.setColor(ContextCompat.getColor(listener as Context, R.color.priority_critical))
        }

        when (task.priority) {
            0 -> (taskViewHolder.priority.background as? GradientDrawable)
                ?.setColor(ContextCompat.getColor(listener as Context, R.color.priority_critical))
            1 -> (taskViewHolder.priority.background as? GradientDrawable)
                ?.setColor(ContextCompat.getColor(listener as Context, R.color.priority_major))
            2 -> (taskViewHolder.priority.background as? GradientDrawable)
                ?.setColor(ContextCompat.getColor(listener as Context, R.color.priority_normal))
            else -> (taskViewHolder.priority.background as? GradientDrawable)
                ?.setColor(ContextCompat.getColor(listener as Context, R.color.priority_normal))// Default fallback
        }

        val colorRes = when (task.priority) {
            0 -> R.color.priority_critical
            1 -> R.color.priority_major
            2 -> R.color.priority_normal
            else -> R.color.priority_normal
        }
        val color = ContextCompat.getColor(listener as Context, colorRes)

        // Ensure completion state is always properly set
        if (task.isCompleted) {
            taskViewHolder.isCompleted.setImageResource(R.drawable.check_box_24)
            taskViewHolder.isCompleted.setColorFilter(color)
        } else {
            taskViewHolder.isCompleted.setImageResource(R.drawable.check_box_blank_24)
            taskViewHolder.isCompleted.setColorFilter(color)
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
            onTaskClickListener(task)
        }
    }

    override fun getItemCount() = dataSet.size

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return

        Collections.swap(dataSet, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun saveSortOrder() {
        // Update sortOrder values based on current positions
        val updatedTasks = dataSet.mapIndexed { index, task ->
            task.copy(sortOrder = index)
        }
        
        // Update the dataset
        dataSet.clear()
        dataSet.addAll(updatedTasks)
    }

    fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getTasks(): List<Task> = dataSet.toList()

    fun updateData(newData: List<Task>) {
        dataSet.clear()
        dataSet.addAll(newData)
        notifyDataSetChanged()
    }

}
