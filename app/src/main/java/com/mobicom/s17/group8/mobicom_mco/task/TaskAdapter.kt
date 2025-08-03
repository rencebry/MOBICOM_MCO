package com.mobicom.s17.group8.mobicom_mco.task

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemTaskBinding
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedDate
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedTime

class TaskAdapter(
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskClicked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {
    // Implementation of the TodoAdapter class
    // This class will handle the display of todo items in a RecyclerView
    // It will include methods to bind data to views, handle item clicks, etc.

    // Placeholder for the actual implementation
    // You can define your ViewHolder, onCreateViewHolder, onBindViewHolder, etc. here

    var currentTaskListName: String = "Tasks"
    private var taskListMap: Map<String, String> = emptyMap()

    inner class TaskViewHolder(val binding: ListItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Define your ViewHolder class here
        // This class will hold references to the views for each todo item
        @SuppressLint("SetTextI18n")
        fun bindData(task: Task) {
            binding.taskNameTv.text = task.title
            //binding.taskDetails.text = task.details
            binding.taskCheckbox.isChecked = (task.status == "completed")
            val formattedDate = task.due.toFormattedDate()
            val formattedTime = task.due.toFormattedTime()

            val listName = taskListMap[task.tasklistId] ?: "Task"

            binding.taskInfoTv.text = "$listName | $formattedDate $formattedTime".trim()

            binding.taskNameTv.paintFlags = if (task.status == "completed") {
                binding.taskNameTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.taskNameTv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Set the click listener on the whole task item
            binding.root.setOnClickListener {
                // Handle click event of a specific task, e.g., navigate to task details, which is another page
                onTaskClicked(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = TaskViewHolder(binding)

        // Set listeners once here for best performance
        holder.binding.root.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onTaskClicked(getItem(position))
            }
        }

        holder.binding.taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onTaskChecked(getItem(position), isChecked)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    fun submitTaskLists(lists: List<TaskList>) {
        taskListMap = lists.associateBy({ it.id }, { it.title })
        // Redraw items if the task list names change
        notifyDataSetChanged()
    }

    // DiffUtil class for Task entity
    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}