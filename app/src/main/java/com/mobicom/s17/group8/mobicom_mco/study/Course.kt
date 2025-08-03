package com.mobicom.s17.group8.mobicom_mco.study

import androidx.annotation.ColorRes
import java.util.UUID

data class Course(
    val courseId: String = UUID.randomUUID().toString(),
    val courseTitle: String,
    val deckCount: Int,
    @ColorRes val colorResId: Int
)
