package com.mobicom.s17.group8.mobicom_mco.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class TasksViewModel(private val repository: TaskRepository, private val userId: String) : ViewModel() {

    // Flow to observe all task lists
    @OptIn(ExperimentalCoroutinesApi::class)
    val allTaskLists: StateFlow<List<TaskList>> =
        repository.getTaskListsForUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Keep track of the currently selected task list ID
    private val _selectedTaskListId = MutableStateFlow<String?>(null)
    val selectedTaskListId : StateFlow<String?> = _selectedTaskListId.asStateFlow()

    // Expose a flow of tasks that automatically updates when the selected ID changes
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedList: StateFlow<List<Task>> = _selectedTaskListId.flatMapLatest { id ->
        if (id == null) {
            flowOf(emptyList()) // Return an empty flow if no list is selected
        } else {
            repository.getTasksByListId(id)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectTaskList(taskListId: String?) {
        _selectedTaskListId.value = taskListId
    }

    // Function to add new task list
    fun insertNewTaskList(listName: String) {
        viewModelScope.launch{
            val newList = TaskList(
                // TODO: Check if UUID is compatible with Google Tasks API id string format
                id = UUID.randomUUID().toString(),
                userId = this@TasksViewModel.userId,
                title = listName,
                updated = Instant.now().toString(),
                isSynced = false,
                isDeleted = false,
                isDeletable = true
            )
            repository.insertTaskList(newList)
        }
    }

    // Function to update an existing task list
    fun updateTaskList(taskList: TaskList) {
        viewModelScope.launch {
            repository.updateTaskList(taskList)
        }
    }

    // Function to delete a task list
    fun deleteTaskList(taskList: TaskList) {
        viewModelScope.launch {
            repository.deleteTaskList(taskList)
        }
    }

    // ViewModelFactory to pass the repository to the ViewModel
}