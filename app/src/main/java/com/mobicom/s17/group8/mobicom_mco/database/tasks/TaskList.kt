package com.mobicom.s17.group8.mobicom_mco.database.tasks

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mobicom.s17.group8.mobicom_mco.database.user.User

@Entity(tableName = "tasklists",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["uid"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )])
data class TaskList(
    @PrimaryKey val id : String, // Unique identifier for the task list
    val userId : String, // ID of the user who owns this task list
    var title : String,
    val updated : String, // RFC 3339 timestamp of the last modification (output only)
    val isSynced : Boolean = false, // Indicates if the task list is synced with the server/API (default is false)
    val isDeleted : Boolean = false, // Indicates if the task list is deleted (default is false)
    val isDeletable: Boolean = true // Default true
)