package com.mobicom.s17.group8.mobicom_mco.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskRepository
import java.lang.IllegalArgumentException

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val userId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            return TasksViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}