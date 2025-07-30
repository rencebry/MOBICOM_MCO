package com.mobicom.s17.group8.mobicom_mco.task

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemTaskBinding
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedDate
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedTime

class TaskAdapter(
    private val onTaskChecked: (Task, Boolean) -> Unit
    // private val onTaskClicked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {
    // Implementation of the TodoAdapter class
    // This class will handle the display of todo items in a RecyclerView
    // It will include methods to bind data to views, handle item clicks, etc.

    // Placeholder for the actual implementation
    // You can define your ViewHolder, onCreateViewHolder, onBindViewHolder, etc. here

    var currentTaskListName: String = "Tasks" // Default task list name

    inner class TaskViewHolder(val binding : ListItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Define your ViewHolder class here
        // This class will hold references to the views for each todo item
        @SuppressLint("SetTextI18n")
        fun bindData(task : Task) {
            binding.taskNameTv.text = task.title
            //binding.taskDetails.text = task.details
            binding.taskCheckbox.isChecked = (task.status == "completed")
            val formattedDate = task.due.toFormattedDate()
            val formattedTime = task.due.toFormattedTime()

            // TODO: Change according to updated task entity (due date and time should be parsed from the "due" field)
            binding.taskInfoTv.text = currentTaskListName +
                    when {
                        formattedDate != null && formattedTime != null -> " | $formattedDate $formattedTime"
                        formattedDate != null -> " | $formattedDate"
                        // formattedTime != null -> " | $formattedTime"
                        else -> ""
                    }

//            // Listener for checkbox state change
//            binding.taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
//                task.isCompleted = isChecked
//                task.status = if (isChecked) "completed" else "needsAction"
//            }

            if (task.status == "completed") {
                binding.taskNameTv.paintFlags =
                    binding.taskNameTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.taskNameTv.paintFlags =
                    binding.taskNameTv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }


//            binding.root.setOnClickListener {
//                // Handle click event of a specific task, e.g., navigate to task details, which is another page
//                onTaskClicked(task)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = TaskViewHolder(binding)

        return holder
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bindData(task)

        holder.binding.taskCheckbox.setOnCheckedChangeListener(null)
        holder.binding.taskCheckbox.isChecked = (task.status == "completed")
        holder.binding.taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
            onTaskChecked(task, isChecked)
        }
    }
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