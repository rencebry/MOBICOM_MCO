package com.mobicom.s17.group8.mobicom_mco.database.study

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "decks",
    foreignKeys = [ForeignKey(
        entity = Course::class,
        parentColumns = ["courseId"],
        childColumns = ["courseId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Deck(
    @PrimaryKey val deckId: String = "",
    val courseId: String = "",
    val deckTitle: String = "",
    val dateCreated: String = "",
    val cardCount: Int = 0,
    val isFavorite: Boolean = false,
    val userId: String = ""
)