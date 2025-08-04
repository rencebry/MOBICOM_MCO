package com.mobicom.s17.group8.mobicom_mco.api

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.TasksScopes
import com.google.api.services.tasks.model.Task
import com.google.api.services.tasks.model.TaskList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.api.client.util.DateTime
import java.time.Instant

class TasksApiService (
    private val context: Context,
    private val googleSignInAccount: GoogleSignInAccount
){
    private val tasksService: Tasks

    init {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(TasksScopes.TASKS)
        ). also {
            it.selectedAccount = googleSignInAccount.account
        }

        tasksService = Tasks.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("Study With Me App")
            .build()
    }

    suspend fun getAllTaskLists(lastSyncTimestamp: String? = null): List<com.google.api.services.tasks.model.TaskList> {
        return withContext(Dispatchers.IO) {
            try {
                val allTaskLists = tasksService.tasklists().list().execute()
                val items = allTaskLists.items ?: emptyList()

                if (lastSyncTimestamp != null) {
                    return@withContext items
                }
                val lastSyncInstant = Instant.parse(lastSyncTimestamp)
                items.filter { apiList ->
                    val updatedDateTime = apiList.updated ?: return@filter false
                    val updatedInstant = Instant.parse(updatedDateTime)
                    updatedInstant.isAfter(lastSyncInstant)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // Return an empty list in case of error
            }
        }
    }

    suspend fun getTasksForList(taskListId: String, lastSyncTimestamp: String? = null): List<com.google.api.services.tasks.model.Task> {
        return withContext(Dispatchers.IO) {
            try {
                val request = tasksService.tasks().list(taskListId)
                if (lastSyncTimestamp != null) {
                    request.updatedMin = lastSyncTimestamp
                }
                request.showCompleted = true

                val tasks = request.execute()
                tasks.items ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // TASK LIST WRITE METHODS
    suspend fun createTaskList(title: String): TaskList? {
        return withContext(Dispatchers.IO) {
            try {
                val newApiList = TaskList().apply { this.title = title }
                tasksService.tasklists().insert(newApiList).execute()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun updateTaskList(listId: String, newTitle: String): TaskList? {
        return withContext(Dispatchers.IO) {
            try {
                val updatedApiList = TaskList().apply { this.title = newTitle }
                tasksService.tasklists().patch(listId, updatedApiList).execute()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun deleteTaskList(listId: String){
        withContext(Dispatchers.IO) {
            try {
                tasksService.tasklists().delete(listId).execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // TASK WRITE METHODS
    suspend fun createTask(taskListId: String, localTask: com.mobicom.s17.group8.mobicom_mco.database.tasks.Task): Task? {
        return withContext(Dispatchers.IO) {
            try {
                val apiTask = Task().apply {
                    title = localTask.title
                    notes = localTask.notes
                    due = localTask.due
                    status = localTask.status
                }
                tasksService.tasks().insert(taskListId, apiTask).execute()
            } catch (e: Exception) { e.printStackTrace(); null }
        }
    }

    suspend fun updateTask(taskListId: String, localTask: com.mobicom.s17.group8.mobicom_mco.database.tasks.Task): Task? {
        return withContext(Dispatchers.IO) {
            try {
                val apiTask = Task().apply {
                    id = localTask.id
                    title = localTask.title
                    notes = localTask.notes
                    due = localTask.due
                    status = localTask.status
                }
                tasksService.tasks().patch(taskListId, apiTask.id, apiTask).execute()
            } catch (e: Exception) { e.printStackTrace(); null }
        }
    }

    suspend fun deleteTask(taskListId: String, taskId: String) {
        withContext(Dispatchers.IO) {
            try {
                tasksService.tasks().delete(taskListId, taskId).execute()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}