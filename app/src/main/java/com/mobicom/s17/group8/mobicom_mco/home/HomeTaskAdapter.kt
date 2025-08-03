package com.mobicom.s17.group8.mobicom_mco.home

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

class HomeTaskAdapter(
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskClicked: (Task) -> Unit
) : ListAdapter<Task, HomeTaskAdapter.TaskViewHolder>(HomeTaskDiffCallback()) {

    private var taskListMap: Map<String, String> = emptyMap()

    inner class TaskViewHolder(val binding: ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(task: Task) {
            binding.apply {
                taskNameTv.text = task.title

                taskNameTv.paintFlags = if (task.status == "completed") {
                    taskNameTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    taskNameTv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                val formattedDate = task.due?.toFormattedDate() ?: ""
                val formattedTime = task.due?.toFormattedTime() ?: ""

                val listName = taskListMap[task.tasklistId] ?: "Task"

                taskInfoTv.text = "$listName | $formattedDate $formattedTime".trim()

                taskCheckbox.setOnCheckedChangeListener(null)
                taskCheckbox.isChecked = (task.status == "completed")
                taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    onTaskChecked(task, isChecked)
                }

                // Handle item clicks
                root.setOnClickListener {
                    onTaskClicked(task)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun submitTaskLists(lists: List<com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList>) {
        taskListMap = lists.associateBy({ it.id }, { it.title })
        notifyDataSetChanged()
    }
}

class HomeTaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}