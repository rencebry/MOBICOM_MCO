package com.mobicom.s17.group8.mobicom_mco.database.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.mobicom.s17.group8.mobicom_mco.database.user.User

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = TaskList::class,
        parentColumns = ["id"],
        childColumns = ["tasklistId"],
        onDelete = ForeignKey.CASCADE // When a task list is deleted, all tasks in that list are also deleted
    ),
    ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // When a user is deleted, all their task lists and tasks are also deleted
    )]
)
data class Task(
    @PrimaryKey val id : String,
    val userId: String, // ID of the user who owns this task
    val tasklistId : String, // ID of the task list this task belongs to
    var title : String, // max length 1024 characters
    var status : String, // needsAction, completed
    var due : String?, // convert dueDate and dueTime into RFC 3339 timestamp
    var notes : String? = null, // aka details, max length 8192 characters
    var updated : String, // Last modification timestamp (RFC 3339 timestamp) (output only)
    var completed : String?, // completion date of the task (timestamp). field s omitted if task is not complete
    val parent : String?, // ID of the parent task (if any)
    val position : String?, // Position of the task in the list (used for ordering)
    var isSynced : Boolean = false, // true if the task is synced with the server/API, default is false
    var isDeleted : Boolean = false, // true if the task is deleted, default is false

    var isCompleted : Boolean = false, // true if the task is completed, will remove later i think
    val dueDate : String? = null,
    val dueTime : String? = null
)
