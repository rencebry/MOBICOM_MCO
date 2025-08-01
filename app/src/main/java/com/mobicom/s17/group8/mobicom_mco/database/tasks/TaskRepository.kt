package com.mobicom.s17.group8.mobicom_mco.database.tasks

import kotlinx.coroutines.flow.Flow

/**
* This repository class acts as a mediator between the data source (DAO) and the ViewModel.
 * It exposes all necessary data operations from the DAOs, providing a clean API for data access.
 *
* */

class TaskRepository (
    private val taskDao: TaskDao,
    private val taskListDao: TaskListDao
){
    fun getTaskListsForUser(userId: String): Flow<List<TaskList>> {
        return taskListDao.getTaskListsForUser(userId)
    }

    fun getTasksByListId(tasklistId: String): Flow<List<Task>> {
        return taskDao.getTasksByListId(tasklistId)
    }

    suspend fun getTaskListById(id: String): TaskList? {
        return taskListDao.getTaskListById(id)
    }

    suspend fun getTaskById(id: String): Task? {
        return taskDao.getTaskById(id)
    }

    // Write operations for TaskLists (Suspend functions)
    suspend fun insertTaskList(taskList: TaskList) {
        taskListDao.insertTaskList(taskList)
    }

    suspend fun updateTaskList(taskList: TaskList) {
        taskListDao.updateTaskList(taskList)
    }

    suspend fun deleteTaskList(taskList: TaskList) {
        taskListDao.deleteTaskList(taskList)
    }

    suspend fun deleteTaskListByIdIfDeletable(id: String) {
        taskListDao.deleteTaskListByIdIfDeletable(id)
    }

    // Write operations for Tasks (Suspend functions)

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}