package com.mobicom.s17.group8.mobicom_mco.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemTasklistBinding
import com.mobicom.s17.group8.mobicom_mco.databinding.ItemAddNewListBinding

private const val ITEM_VIEW_TYPE_TASK_LIST = 0
private const val ITEM_VIEW_TYPE_ADD_BUTTON = 1

class TaskListAdapter (
    private val onTaskListClicked: (TaskList) -> Unit,
    private val onAddNewListClicked: () -> Unit)
    : ListAdapter<TaskList, RecyclerView.ViewHolder>(TasklistDiffCallback()){

    private var selectedId: String? = null

    fun setSelectedId(id: String?) {
        val oldSelectedId = selectedId
        selectedId = id

        oldSelectedId?.let {
            val oldPosition = currentList.indexOfFirst { taskList -> taskList.id == it }
            if (oldPosition != -1) notifyItemChanged(oldPosition)
        }
        selectedId?.let {
            val newPosition = currentList.indexOfFirst { taskList -> taskList.id == it }
            if (newPosition != -1) notifyItemChanged(newPosition)
        }
    }
    // ViewHolder for normal TaskList items
    inner class TasklistViewHolder(private val binding: ListItemTasklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // bind function to set the data
        fun bindData(taskList: TaskList, clickListener: (TaskList) -> Unit) {
            binding.taskListTv.text = taskList.title
            binding.root.setOnClickListener {
                clickListener(taskList)
            }
            itemView.isSelected = (taskList.id == selectedId)
        }
    }

    // ViewHolder for the "+ New List" button
    class AddNewListViewHolder(private val binding: ItemAddNewListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // bind function to set the data
        fun bindData(clickListener: () -> Unit) {
            binding.root.setOnClickListener { clickListener() }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        // If the position is the last one, return the type for the add button
        return if (position == super.getItemCount()) {
            ITEM_VIEW_TYPE_ADD_BUTTON
        } else {
            ITEM_VIEW_TYPE_TASK_LIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Inflate correct layout based on the view type
        return when (viewType) {
            ITEM_VIEW_TYPE_TASK_LIST -> {
                val binding = ListItemTasklistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TasklistViewHolder(binding)
            }
            ITEM_VIEW_TYPE_ADD_BUTTON -> {
                val binding = ItemAddNewListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddNewListViewHolder(binding).apply {
                    binding.root.setOnClickListener { onAddNewListClicked() } // TODO: double check this
                }
            }
            else -> throw IllegalArgumentException("Invalid view type")
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TasklistViewHolder -> {
                val taskList = getItem(position)
                holder.bindData(taskList, onTaskListClicked)
            }
            is AddNewListViewHolder -> {
                holder.bindData(onAddNewListClicked)
            }
        }
    }
}


// Helper class for calculating differences between two lists
class TasklistDiffCallback : DiffUtil.ItemCallback<TaskList>() {
    override fun areItemsTheSame(oldItem: TaskList, newItem: TaskList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TaskList, newItem: TaskList): Boolean {
        return oldItem == newItem
    }
}