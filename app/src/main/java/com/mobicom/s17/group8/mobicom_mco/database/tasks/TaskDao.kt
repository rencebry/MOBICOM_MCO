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
}