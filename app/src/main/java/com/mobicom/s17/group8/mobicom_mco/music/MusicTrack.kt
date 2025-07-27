package com.mobicom.s17.group8.mobicom_mco.music

import androidx.annotation.ColorRes
import androidx.annotation.RawRes

data class MusicTrack(
    val name: String,
    @ColorRes val centerColorResId: Int,
    @RawRes val trackResId: Int
)