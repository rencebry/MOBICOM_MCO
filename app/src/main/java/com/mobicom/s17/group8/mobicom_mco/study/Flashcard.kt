package com.mobicom.s17.group8.mobicom_mco.study

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Flashcard(
    val flashcardId: String = UUID.randomUUID().toString(),
    var deckId: String = "",
    var courseId: String = "",
    var question: String = "",
    var answer: String = "",
    val isFavorite: Boolean = false,
    val isFlipped: Boolean = false
) : Parcelable
