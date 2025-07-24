package com.mobicom.s17.group8.mobicom_mco.database

import androidx.room.*

@Dao
interface TaskListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskList(taskList: TaskList)

    @Update
    suspend fun updateTaskList(taskList: TaskList)

    @Delete
    suspend fun deleteTaskList(taskList: TaskList)

    @Query("DELETE FROM tasklists WHERE id = :id AND isDeletable = 1")
    suspend fun deleteTaskListByIdIfDeletable(id: String)

    @Query("SELECT * FROM tasklists WHERE id = :id")
    suspend fun getTaskListById(id: String): TaskList?

    @Query("SELECT * FROM tasklists")
    suspend fun getAllTaskLists(): List<TaskList>

//    @Query("DELETE FROM tasklists WHERE isDeleted = 1")
//    suspend fun deleteAllDeletedTaskLists()
}