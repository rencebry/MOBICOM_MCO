package com.mobicom.s17.group8.mobicom_mco.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

enum class TaskFilter { ONGOING, ALL, MISSED, COMPLETED }

class TasksViewModel(private val repository: TaskRepository, private val userId: String) : ViewModel() {

    private val _viewedTask = MutableStateFlow<Task?>(null)
    val viewedTask: StateFlow<Task?> = _viewedTask.asStateFlow()

    private val _taskMarkedCompleteEvent = MutableSharedFlow<Task>()
    val taskMarkedCompleteEvent = _taskMarkedCompleteEvent.asSharedFlow()
    private var taskBeforeCompletion: Task? = null

    private var taskBeforeToggle: Task? = null

    private val _taskFilter = MutableStateFlow(TaskFilter.ONGOING)
    val taskFilter: StateFlow<TaskFilter> = _taskFilter

    fun loadTaskDetails(taskId: String){
        viewModelScope.launch {
            _viewedTask.value = repository.getTaskById(taskId)
        }
    }

    val upcomingTasks = getUpcomingTasks(userId).asLiveData()

    private fun getUpcomingTasks(userId: String): Flow<List<Task>> {
        val formatter = DateTimeFormatter.ISO_INSTANT
        val now = ZonedDateTime.now()
        val sevenDaysFromNow = now.plusDays(7).withHour(23).withMinute(59).withSecond(59)

        val startDateString = formatter.format(now.toInstant())
        val endDateString = formatter.format(sevenDaysFromNow.toInstant())

        return repository.getUpcomingTasks(userId, startDateString, endDateString)
    }
    // Flow to observe all task lists
    @OptIn(ExperimentalCoroutinesApi::class)
    val allTaskLists: StateFlow<List<TaskList>> =
        repository.getTaskListsForUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTaskListId = MutableStateFlow<String?>(ALL_TASKS_ID)
    val selectedTaskListId : StateFlow<String?> = _selectedTaskListId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedList: StateFlow<List<Task>> = combine(selectedTaskListId, _taskFilter) { listId, filter ->
        Pair(listId, filter)
    }.flatMapLatest { (listId, filter) ->
        if (listId == null) {
            flowOf(emptyList())
        } else if (listId == ALL_TASKS_ID) {
            repository.getAllTasksForUser(userId)
        }else {
            val currentDate = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            when (filter) {
                TaskFilter.ONGOING -> repository.getOngoingTasksInList(listId, currentDate)
                TaskFilter.ALL -> repository.getAllTasksInList(listId)
                TaskFilter.MISSED -> repository.getMissedTasksInList(listId, currentDate)
                TaskFilter.COMPLETED -> repository.getCompletedTasksInList(listId)
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Start collecting when the UI is visible
            initialValue = emptyList() // Start with an empty list
        )

    fun setFilter(filter: TaskFilter) {
        _taskFilter.value = filter
    }


    fun selectTaskList(taskListId: String?) {
        _selectedTaskListId.value = taskListId
    }

    // Function to add new task list
    fun insertNewTaskList(listName: String) {
        viewModelScope.launch{
            val newList = TaskList(
                // TODO: Check if UUID is compatible with Google Tasks API id string format
                id = "temp_${UUID.randomUUID()}", // Use a temporary ID for local storage
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

    // Function to delete a task list (soft delete)
    fun deleteTaskList(taskList: TaskList) {
        viewModelScope.launch {
            val deletedList = taskList.copy(
                isDeleted = true,
                isSynced = false // Mark as unsynced
            )
            repository.updateTaskList(deletedList)
        }
    }

    // Function to add a new task
    fun addNewTask(title: String, notes: String? ,due: String?, taskListId: String) {
        viewModelScope.launch {
            val newTask = Task(
                id = "temp_${UUID.randomUUID()}", // Use a temporary ID for local storage
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

    fun updateTask(title: String, notes: String?, due: String?, taskListId: String) {
        val currentTask = _viewedTask.value ?: return

        if(currentTask.title == title && currentTask.notes == notes && currentTask.due == due && currentTask.tasklistId == taskListId) {
            return // No changes to update
        }
        viewModelScope.launch {
            val updatedTask = currentTask.copy(
                title = title,
                notes = notes,
                due = due,
                tasklistId = taskListId,
                updated = Instant.now().toString(),
                isSynced = false // Mark as unsynced
            )
            repository.updateTask(updatedTask)
            _viewedTask.value = updatedTask // Update the viewed task
        }
    }

    // Function to delete the currently viewed task (soft delete)
    fun deleteTask() {
        _viewedTask.value?.let { taskToDelete ->
            viewModelScope.launch {
                val deletedTask = taskToDelete.copy(
                    isDeleted = true,
                    isSynced = false // Mark as unsynced
                )
                repository.updateTask(deletedTask)
            }
        }
    }

    // Mark completed logic with Undo
    fun toggleTaskCompletion() {
        val currentTask = _viewedTask.value ?: return
        taskBeforeToggle = currentTask

        val isNowCompleted = (currentTask.status != "completed")
        val newStatus = if (isNowCompleted) "completed" else "needsAction"
        val newCompletedTimestamp = if (isNowCompleted) Instant.now().toString() else null

        viewModelScope.launch {
            val toggledTask = currentTask.copy(
                status = newStatus,
                completed = newCompletedTimestamp
            )
            repository.updateTask(toggledTask)
            if (isNowCompleted){
                _taskMarkedCompleteEvent.emit(toggledTask) // Send event to show Snackbar
            } else {
                _viewedTask.value = toggledTask
            }
        }
    }

    fun undoToggleCompletion() {
        taskBeforeToggle?.let { originalTask ->
            viewModelScope.launch {
                repository.updateTask(originalTask)
                _viewedTask.value = originalTask // Restore the original task state
                taskBeforeToggle = null
            }
        }
    }
}