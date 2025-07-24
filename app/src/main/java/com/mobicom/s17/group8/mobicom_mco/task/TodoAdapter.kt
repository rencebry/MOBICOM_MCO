package com.mobicom.s17.group8.mobicom_mco.task

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.database.Task
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemTaskBinding

class TodoAdapter(
    private val todoItems: List<Task>,
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    // Implementation of the TodoAdapter class
    // This class will handle the display of todo items in a RecyclerView
    // It will include methods to bind data to views, handle item clicks, etc.

    // Placeholder for the actual implementation
    // You can define your ViewHolder, onCreateViewHolder, onBindViewHolder, etc. here

    inner class TodoViewHolder(val binding : ListItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Define your ViewHolder class here
        // This class will hold references to the views for each todo item
        @SuppressLint("SetTextI18n")
        fun bindData(task : Task) {
            binding.taskNameTv.text = task.title
            //binding.taskDetails.text = task.details
            binding.taskCheckbox.isChecked = task.isCompleted

//            if (task.isStarred) {
//                binding.starredIv.visibility = android.view.View.VISIBLE
//            } else {
//                binding.starredIv.visibility = android.view.View.GONE
//            }
            //binding.starredIv = task.isStarred
            binding.taskInfoTv.text = task.label +
                    when {
                        task.dueDate != null && task.dueTime != null -> " | ${task.dueDate} ${task.dueTime}"
                        task.dueDate != null -> " | ${task.dueDate}"
                        task.dueTime != null -> " | ${task.dueTime}"
                        else -> ""
                    }

            // Listener for checkbox state change
            binding.taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
            }


//            binding.root.setOnClickListener {
//                // Handle click event of a specific task, e.g., navigate to task details, which is another page
//                onTaskClicked(task)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = TodoViewHolder(binding)

        return holder
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val task = todoItems[position]
        holder.bindData(task)
    }

    override fun getItemCount() = todoItems.size
}