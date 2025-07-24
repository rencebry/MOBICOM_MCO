package com.mobicom.s17.group8.mobicom_mco.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasklists")
data class TaskList(
    @PrimaryKey val id : String, // Unique identifier for the task list
    val title : String,
    val updated : String,
    val isSynced : Boolean = false, // Indicates if the task list is synced with the server/API (default is false)
    val isDeleted : Boolean = false, // Indicates if the task list is deleted (default is false)
    val isDeletable: Boolean = true // Default true

)
