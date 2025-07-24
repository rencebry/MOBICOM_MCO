package com.mobicom.s17.group8.mobicom_mco.study

import androidx.annotation.ColorRes

data class Course(
    val id: String,
    val name: String,
    val deckCount: Int,
    @ColorRes val colorResId: Int
)