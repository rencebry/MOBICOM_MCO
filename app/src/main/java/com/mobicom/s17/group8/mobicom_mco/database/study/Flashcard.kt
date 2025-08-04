package com.mobicom.s17.group8.mobicom_mco.database.study

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.room.ColumnInfo

@Entity(
    tableName = "flashcards",
    foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = ["deckId"],
        childColumns = ["deckId"],
        onDelete = ForeignKey.CASCADE
    )]
)
@Parcelize
data class Flashcard(
    @PrimaryKey val flashcardId: String = "",
    val deckId: String = "",
    val courseId: String = "",
    val question: String = "",
    val answer: String = "",
    val isFavorite: Boolean = false
) : Parcelable