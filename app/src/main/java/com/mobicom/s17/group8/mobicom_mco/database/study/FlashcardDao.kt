package com.mobicom.s17.group8.mobicom_mco.database.study

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FlashcardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Query("DELETE FROM flashcards WHERE flashcardId = :flashcardId")
    suspend fun deleteFlashcardById(flashcardId: String)

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY flashcardId")
    fun getFlashcardsForDeck(deckId: String): LiveData<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE courseId = :courseId")
    fun getFlashcardsForCourse(courseId: String): LiveData<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    suspend fun getFlashcardsForDeckSync(deckId: String): List<Flashcard>

    @Query("SELECT COUNT(flashcardId) FROM flashcards WHERE deckId = :deckId")
    suspend fun getFlashcardCountForDeck(deckId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(flashcards: List<Flashcard>)


}