package com.mobicom.s17.group8.mobicom_mco.sync

import android.util.Log
import com.mobicom.s17.group8.mobicom_mco.api.TasksApiService
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskRepository
import java.time.Instant

class SyncRepository (
    private val tasksApiService: TasksApiService,
    private val taskRepository: TaskRepository,
    private val userId: String
){
    suspend fun performInitialSync() {
        Log.d("SyncRepository", "Starting initial sync for user: $userId")

        val apiTaskLists = tasksApiService.getAllTaskLists()
        Log.d("SyncRepository", "Fetched ${apiTaskLists.size} task lists from API.")

        for (apiList in apiTaskLists) {
            val roomTaskList = TaskList(
                id = apiList.id,
                userId = this.userId,
                title = apiList.title,
                updated = apiList.updated,
                //updated = apiList.updated ?: Instant.now().toString(),
                isDeletable = true // Assuming all lists from API are user-created
            )
            taskRepository.insertTaskList(roomTaskList)
            Log.d("SyncRepository", "Saved TaskList: ${roomTaskList.title}")

            val apiTasks = tasksApiService.getTasksForList(apiList.id)
            Log.d("SyncRepository", "Fetched ${apiTasks.size} tasks for list '${apiList.title}'.")

            for (apiTask in apiTasks) {
                if (apiTask.title == null || apiTask.title.isBlank()) continue

                val roomTask = Task(
                    id = apiTask.id,
                    userId = this.userId,
                    tasklistId = apiList.id,
                    title = apiTask.title,
                    status = apiTask.status ?: "needsAction",
                    due = apiTask.due,
                    notes = apiTask.notes,
                    updated = apiTask.updated,
                    completed = apiTask.completed,
                    parent = apiTask.parent,
                    position = apiTask.position,
                    isSynced = true, // Mark as synced since we just fetched it
                    isDeleted = apiTask.deleted ?: false // Not deleted, just fetched
                )
                taskRepository.insertTask(roomTask)
            }
        }
        Log.d("SyncRepository", "Initial sync completed.")
    }

    suspend fun uploadLocalChanges() {
        Log.d("SyncRepository", "Starting upload of local changes...")

        // 1. UPLOAD TASK LIST CHANGES
        val unsyncedLists = taskRepository.getUnsyncedTaskLists(userId)
        Log.d("SyncRepository", "Found ${unsyncedLists.size} unsynced task lists.")
        for (list in unsyncedLists) {
            if (list.isDeleted) {
                // Handle Deletion
                if (!list.id.startsWith("temp_")) { // Only delete if it exists on the server
                    tasksApiService.deleteTaskList(list.id)
                }
                taskRepository.deleteTaskListPermanently(list) // Hard delete from local DB
            } else if (list.id.startsWith("temp_")) {
                // Handle Creation
                val newListFromApi = tasksApiService.createTaskList(list.title)
                if (newListFromApi != null) {
                    // CRUCIAL: Update local item with real ID from API
                    taskRepository.updateLocalListId(list.id, newListFromApi.id)
                }
            } else {
                // Handle Update
                tasksApiService.updateTaskList(list.id, list.title)
                taskRepository.markListAsSynced(list.id)
            }
        }

        // 2. UPLOAD TASK CHANGES (similar logic)
        val unsyncedTasks = taskRepository.getUnsyncedTasks(userId)
        Log.d("SyncRepository", "Found ${unsyncedTasks.size} unsynced tasks.")
        for (task in unsyncedTasks) {
            // Skip syncing tasks whose parent list is still temporary
            if (task.tasklistId.startsWith("temp_")) continue

            if (task.isDeleted) {
                if (!task.id.startsWith("temp_")) {
                    tasksApiService.deleteTask(task.tasklistId, task.id)
                }
                taskRepository.deleteTaskPermanently(task.id)
            } else if (task.id.startsWith("temp_")) {
                val newTaskFromApi = tasksApiService.createTask(task.tasklistId, task)
                if (newTaskFromApi != null) {
                    taskRepository.updateLocalTaskId(task.id, newTaskFromApi.id)
                }
            } else {
                tasksApiService.updateTask(task.tasklistId, task)
                taskRepository.markTaskAsSynced(task.id)
            }
        }


        Log.d("SyncRepository", "Upload of local changes finished.")
    }

    suspend fun downloadCloudChanges(lastSyncTimestamp: String?) {
        Log.d("SyncRepository", "Starting download of cloud changes since: $lastSyncTimestamp")

        val changedApiLists = tasksApiService.getAllTaskLists(lastSyncTimestamp)
        Log.d("SyncRepository", "Found ${changedApiLists.size} changed task lists since last sync.")
        for(apiList in changedApiLists) {
            val roomList = TaskList(
                id = apiList.id,
                userId = this.userId,
                title = apiList.title,
                updated = apiList.updated,
                isSynced = true,
                isDeletable = true
            )
            taskRepository.insertTaskList(roomList)

        }

        val allLocalLists = taskRepository.getAllTaskListsForUser(userId)
        for(localList in allLocalLists) {
            val changedApiTasks = tasksApiService.getTasksForList(localList.id, lastSyncTimestamp)
            if(changedApiTasks.isNotEmpty()) {
                Log.d("SyncRepository", "Found ${changedApiTasks.size} changed tasks in list '${localList.title}'.")
            }

            for (apiTask in changedApiTasks) {
                if (apiTask.deleted == true || apiTask.status == "deleted") {
                    taskRepository.deleteTaskPermanently(apiTask.id)
                } else {
                    if (apiTask.title.isNullOrBlank()) continue

                    val roomTask = Task(
                        id = apiTask.id,
                        userId = this.userId,
                        tasklistId = localList.id,
                        title = apiTask.title,
                        status = apiTask.status ?: "needsAction",
                        due = apiTask.due,
                        notes = apiTask.notes,
                        updated = apiTask.updated,
                        completed = apiTask.completed,
                        parent = apiTask.parent,
                        position = apiTask.position,
                        isSynced = true,
                        isDeleted = false
                    )
                    taskRepository.insertTask(roomTask)
                }
            }
        }
        Log.d("SyncRepository", "Download of cloud changes finished.")
    }

    private suspend fun reconcileDeletedLists() {
        Log.d("SyncRepository", "Reconciling deleted lists...")

        // 1. Get all list IDs from the API server
        val remoteListIds = tasksApiService.getAllTaskLists().map { it.id }.toSet()

        // 2. Get all list IDs from the local database
        val localLists = taskRepository.getAllTaskListsForUser(userId)
        val localListIds = localLists.map { it.id }.toSet()

        // 3. Find which local IDs are no longer on the server
        val listsToDelete = localListIds - remoteListIds

        if (listsToDelete.isNotEmpty()) {
            Log.d("SyncRepository", "Found ${listsToDelete.size} lists to delete locally.")
            // 4. Delete the orphaned lists from the local database
            for (listId in listsToDelete) {
                // We need a new DAO/Repo method for this
                taskRepository.deleteTaskListPermanentlyById(listId)
            }
        }
    }

    suspend fun performSync(lastSyncTimestamp: String?) : String {
        Log.d("SyncRepository", "Starting sync process...")

        val newSyncTimestamp = Instant.now().toString()

        uploadLocalChanges()
        downloadCloudChanges(lastSyncTimestamp)

        reconcileDeletedLists()

        Log.d("SyncRepository", "Full Sync Cycle Finished")
        return newSyncTimestamp
    }

}