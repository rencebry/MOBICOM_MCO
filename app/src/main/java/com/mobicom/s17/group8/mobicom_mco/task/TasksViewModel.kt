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

    // Function to rename an existing task list
    fun renameTaskList(taskList: TaskList, newTitle: String) {
        viewModelScope.launch {
            val updatedList = taskList.copy(
                title = newTitle,
                updated = Instant.now().toString(),
                isSynced = false // Mark as unsynced
            )
            repository.updateTaskList(updatedList)
        }
    }

    // Function to delete a task list
    fun deleteTaskList(taskList: TaskList) {
        viewModelScope.launch {
            repository.deleteTaskList(taskList)
        }
    }

    // Function to add a new task
    fun addNewTask(title: String, notes: String? ,due: String?, taskListId: String) {
        viewModelScope.launch {
            val newTask = Task(
                id = UUID.randomUUID().toString(),
                userId = this@TasksViewModel.userId,
                tasklistId = taskListId,
                title = title,
                status = "needsAction", // Default status for new tasks
                due = due,
                notes = notes,
                updated = Instant.now().toString(),
                completed = null,
                parent = null,
                position = "0",
                isSynced = false,
                isDeleted = false,
            )
            repository.insertTask(newTask)
        }
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            val newStatus = if (isChecked) "completed" else "needsAction"

            if (task.status == newStatus) return@launch // No change needed

            val updatedTask = task.copy(
                status = newStatus,
                completed = if (isChecked) Instant.now().toString() else null, // Set completion time if checked
                //isSynced = false // Mark as unsynced
            )
            repository.updateTask(updatedTask)
        }
    }

    fun editTask(originalTask: Task, newTitle: String, newNotes: String?, newDue: String?) {
        viewModelScope.launch {
            val updatedTask = originalTask.copy(
                title = newTitle,
                notes = newNotes,
                due = newDue,
                updated = Instant.now().toString(),
                //isSynced = false // Mark as unsynced
            )
            repository.updateTask(updatedTask)
        }
    }
    // ViewModelFactory to pass the repository to the ViewModel
}