package com.mobicom.s17.group8.mobicom_mco.study


import java.util.UUID
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deck")
data class Deck(
    @PrimaryKey val deckId: String = UUID.randomUUID().toString(),
    val courseId: String,
    val deckTitle: String,
    val dateCreated: String,
    val cardCount: Int,
    var isFavorite: Boolean = false
)
