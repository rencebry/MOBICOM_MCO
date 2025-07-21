package com.mobicom.s17.group8.mobicom_mco.todo

data class Task(
    val id : String,
    var name : String, // max length 1024 characters
    var updated : String, // Last modification timestamp (RFC 3339 timestamp) (output only)
    var details : String? = null,
    var isCompleted : Boolean = false, // true if the task is completed, will remove later i think
    var status : String, // needsAction, completed
    var due : String? = null, // convert dueDate and dueTime into RFC 3339 timestamp
    var dueDate : String? = null,
    var dueTime : String? = null,
    var label : String? = null, // not present in google tasks API
    var completed : String // completion date of the task (timestamp). field s omitted if task is not complete
)
