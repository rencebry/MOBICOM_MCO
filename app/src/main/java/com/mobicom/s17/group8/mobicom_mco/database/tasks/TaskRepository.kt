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

    suspend fun getAllTaskListsForUser(userId: String): List<TaskList> {
        return taskListDao.getAllTaskListsForUser(userId)
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

    fun getUpcomingTasks(userId: String, startDate: String, endDate: String): Flow<List<Task>> {
        return taskDao.getUpcomingTasksForUser(userId, startDate, endDate)
    }

    fun getAllTasksInList(taskListId: String): Flow<List<Task>> {
        return taskDao.getAllTasksInList(taskListId)
    }

    fun getCompletedTasksInList(taskListId: String): Flow<List<Task>> {
        return taskDao.getCompletedTasksInList(taskListId)
    }

    fun getMissedTasksInList(taskListId: String, currentDate: String): Flow<List<Task>> {
        return taskDao.getMissedTasksInList(taskListId, currentDate)
    }

    fun getOngoingTasksInList(taskListId: String, currentDate: String): Flow<List<Task>> {
        return taskDao.getOngoingTasksInList(taskListId, currentDate)
    }

    fun getAllTasksForUser(userId: String): Flow<List<Task>> {
        return taskDao.getAllTasksForUser(userId)
    }

    // Methods for syncing
    suspend fun getUnsyncedTaskLists(userId: String) = taskListDao.getUnsynced(userId)

    suspend fun getUnsyncedTasks(userId: String) = taskDao.getUnsynced(userId)

    suspend fun updateLocalListId(oldId: String, newId: String) {
        taskListDao.updateIdAndSyncStatus(oldId, newId)
        taskDao.updateTaskListIdForTasks(oldId, newId)
    }

    suspend fun updateLocalTaskId(oldId: String, newId: String) {
        taskDao.updateIdAndSyncStatus(oldId, newId)
    }

    suspend fun markListAsSynced(listId: String) {
        taskListDao.markAsSynced(listId)
    }

    suspend fun markTaskAsSynced(taskId: String) {
        taskDao.markAsSynced(taskId)
    }

    suspend fun deleteTaskListPermanently(taskList: TaskList) {
        taskListDao.deletePermanentlyById(taskList.id)
    }

    suspend fun deleteTaskListPermanentlyById(taskListId: String) {
        taskListDao.deletePermanentlyById(taskListId)
    }

    suspend fun deleteTaskPermanently(taskId: String) {
        taskDao.deletePermanentlyById(taskId)
    }

}