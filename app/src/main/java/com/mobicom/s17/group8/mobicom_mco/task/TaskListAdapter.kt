package com.mobicom.s17.group8.mobicom_mco.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.databinding.ItemAddNewListBinding
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemTasklistBinding

private const val ITEM_VIEW_TYPE_ALL_TASKS = 0
private const val ITEM_VIEW_TYPE_TASK_LIST = 1
private const val ITEM_VIEW_TYPE_ADD_BUTTON = 2

const val ALL_TASKS_ID = "ALL_TASKS_STATIC_ID"

class TaskListAdapter(
    private val onTaskListClicked: (TaskList) -> Unit,
    private val onAllTasksClicked: () -> Unit,
    private val onAddNewListClicked: () -> Unit
) : ListAdapter<TaskList, RecyclerView.ViewHolder>(TasklistDiffCallback()) {

    private var selectedId: String? = ALL_TASKS_ID

    fun setSelectedId(id: String?) {
        val oldSelectedId = selectedId
        selectedId = id

        if (oldSelectedId != null) {
            val oldPosition = if (oldSelectedId == ALL_TASKS_ID) {
                0
            } else {
                currentList.indexOfFirst { it.id == oldSelectedId } + 1
            }
            if (oldPosition > -1) notifyItemChanged(oldPosition)
        }

        if (selectedId != null) {
            val newPosition = if (selectedId == ALL_TASKS_ID) {
                0
            } else {
                currentList.indexOfFirst { it.id == selectedId } + 1
            }
            if (newPosition > -1) notifyItemChanged(newPosition)
        }
    }

    inner class TasklistViewHolder(private val binding: ListItemTasklistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(taskList: TaskList) {
            binding.taskListTv.text = taskList.title
            binding.root.setOnClickListener { onTaskListClicked(taskList) }
            itemView.isSelected = (taskList.id == selectedId)
        }
        fun bindAllTasks() {
            binding.taskListTv.text = "All"
            binding.root.setOnClickListener { onAllTasksClicked() }
            itemView.isSelected = (selectedId == ALL_TASKS_ID)
        }
    }

    // ViewHolder for the "+ New List" button
    class AddNewListViewHolder(binding: ItemAddNewListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ITEM_VIEW_TYPE_ALL_TASKS
            itemCount - 1 -> ITEM_VIEW_TYPE_ADD_BUTTON
            else -> ITEM_VIEW_TYPE_TASK_LIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ALL_TASKS, ITEM_VIEW_TYPE_TASK_LIST -> {
                val binding = ListItemTasklistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TasklistViewHolder(binding)
            }
            ITEM_VIEW_TYPE_ADD_BUTTON -> {
                val binding = ItemAddNewListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                binding.root.setOnClickListener { onAddNewListClicked() }
                AddNewListViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TasklistViewHolder -> {
                if (position == 0) {
                    holder.bindAllTasks()
                } else {
                    val taskList = getItem(position - 1)
                    holder.bindData(taskList)
                }
            }
            is AddNewListViewHolder -> { }
        }
    }
}

// DiffUtil callback remains the same
class TasklistDiffCallback : DiffUtil.ItemCallback<TaskList>() {
    override fun areItemsTheSame(oldItem: TaskList, newItem: TaskList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TaskList, newItem: TaskList): Boolean {
        return oldItem == newItem
    }
}