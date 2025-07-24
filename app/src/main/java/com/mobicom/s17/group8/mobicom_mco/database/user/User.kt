package com.mobicom.s17.group8.mobicom_mco.database.user // A new package for database files is good practice

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class User(
    @PrimaryKey val uid: String,
    val email: String?,
    val displayName: String?,
    val school: String?,
    val course: String?,
    val yearLevel: Int?,
    val profilePictureUrl: String?
)