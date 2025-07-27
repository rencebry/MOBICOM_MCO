package com.mobicom.s17.group8.mobicom_mco.study

import java.util.UUID

data class Flashcard(
    val id: String = UUID.randomUUID().toString(),
    var courseId: String = "",
    var deckId: String = "",
    var question: String = "",
    var answer: String = ""
)