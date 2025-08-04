package com.mobicom.s17.group8.mobicom_mco.database.tasks

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): Task?

    @Query("SELECT * FROM tasks WHERE tasklistId = :tasklistId")
    fun getTasksByListId(tasklistId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getAllTasksForUser(userId: String): Flow<List<Task>>

    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND status = 'needsAction'  
        AND due BETWEEN :startDate AND :endDate 
        ORDER BY due ASC
    """)
    fun getUpcomingTasksForUser(userId: String, startDate: String, endDate: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE tasklistId = :taskListId AND isDeleted = 0 ORDER BY due ASC")
    fun getAllTasksInList(taskListId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE tasklistId = :taskListId AND status = 'completed' AND isDeleted = 0 ORDER BY completed DESC")
    fun getCompletedTasksInList(taskListId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE tasklistId = :taskListId AND status = 'needsAction' AND due < :currentDate AND isDeleted = 0 ORDER BY due ASC")
    fun getMissedTasksInList(taskListId: String, currentDate: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE tasklistId = :taskListId AND status = 'needsAction' AND due >= :currentDate AND isDeleted = 0 ORDER BY due ASC")
    fun getOngoingTasksInList(taskListId: String, currentDate: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsynced(userId: String): List<Task>

    @Query("UPDATE tasks SET isSynced = 1 WHERE id = :taskId")
    suspend fun markAsSynced(taskId: String)

    @Query("UPDATE tasks SET id = :newId, isSynced = 1 WHERE id = :oldId")
    suspend fun updateIdAndSyncStatus(oldId: String, newId: String)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deletePermanentlyById(taskId: String)

    // This will be needed when we update a temp TaskList ID to a real one
    @Query("UPDATE tasks SET tasklistId = :newListId WHERE tasklistId = :oldListId")
    suspend fun updateTaskListIdForTasks(oldListId: String, newListId: String)
}