package com.mobicom.s17.group8.mobicom_mco.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemTaskBinding
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedDate
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedTime

class HomeTaskAdapter(private var tasks: List<Task> = emptyList()) : RecyclerView.Adapter<HomeTaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.apply {
            val formattedDate = task.due?.toFormattedDate() ?: "No date"
            val formattedTime = task.due?.toFormattedTime() ?: ""

            taskNameTv.text = task.title
            taskInfoTv.text = "task label | $formattedDate $formattedTime".trim()
            taskCheckbox.isChecked = (task.status == "completed")
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun submitList(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}