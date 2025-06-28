package com.mobicom.s17.group8.mobicom_mco.todo

data class Task(
    val name: String,
    val details: String? = null,
    var isCompleted: Boolean,
    var isStarred: Boolean,
    val dueDate: String? = null,
    val dueTime: String? = null,
    val label: String? = null
)
