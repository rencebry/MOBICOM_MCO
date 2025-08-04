package com.mobicom.s17.group8.mobicom_mco.database.study

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DeckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck)

    @Update
    suspend fun updateDeck(deck: Deck)

    @Query("DELETE FROM decks WHERE deckId = :deckId")
    suspend fun deleteDeckById(deckId: String)

    @Query("SELECT * FROM decks WHERE courseId = :courseId ORDER BY dateCreated DESC")
    fun getDecksForCourse(courseId: String): LiveData<List<Deck>>
}