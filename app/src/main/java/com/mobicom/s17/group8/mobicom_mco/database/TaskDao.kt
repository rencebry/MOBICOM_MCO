package com.mobicom.s17.group8.mobicom_mco.database

import androidx.room.*

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
    suspend fun getTasksByListId(tasklistId: String): List<Task>

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>
}