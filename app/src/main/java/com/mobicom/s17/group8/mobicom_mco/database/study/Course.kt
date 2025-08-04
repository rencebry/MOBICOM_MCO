package com.mobicom.s17.group8.mobicom_mco.database.study

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.ColorRes
import com.mobicom.s17.group8.mobicom_mco.R // Import R

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey val courseId: String = "",
    val courseTitle: String = "",
    val deckCount: Int = 0,
    @ColorRes val colorResId: Int = R.color.vinyl_blue, // Provide a default color
    val userId: String = ""
)