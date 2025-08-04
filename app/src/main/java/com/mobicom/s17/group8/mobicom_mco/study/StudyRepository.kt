package com.mobicom.s17.group8.mobicom_mco.study

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.database.study.Course
import com.mobicom.s17.group8.mobicom_mco.database.study.CourseDao
import com.mobicom.s17.group8.mobicom_mco.database.study.Deck
import com.mobicom.s17.group8.mobicom_mco.database.study.DeckDao
import com.mobicom.s17.group8.mobicom_mco.database.study.Flashcard
import com.mobicom.s17.group8.mobicom_mco.database.study.FlashcardDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StudyRepository(
    private val courseDao: CourseDao,
    private val deckDao: DeckDao,
    private val flashcardDao: FlashcardDao,
    private val userId: String
) {

    private val firestore = Firebase.firestore
    private val coursesCollection = firestore.collection("users").document(userId).collection("courses")
    private val decksCollection = firestore.collection("users").document(userId).collection("decks")
    private val flashcardsCollection = firestore.collection("users").document(userId).collection("flashcards")

    // --- COURSE DATA ---
    val allCourses: LiveData<List<Course>> = courseDao.getCoursesForUser(userId)

    suspend fun refreshCourses() = withContext(Dispatchers.IO) {
        try {
            val snapshot = coursesCollection.get().await()
            val firestoreCourses = snapshot.toObjects(Course::class.java)
            courseDao.insertAll(firestoreCourses)
            Log.d("StudyRepository", "Courses refreshed successfully.")
        } catch (e: Exception) {
            Log.e("StudyRepository", "Error refreshing courses", e)
        }
    }

    suspend fun addCourse(course: Course) = withContext(Dispatchers.IO) {
        coursesCollection.document(course.courseId).set(course).await()
        courseDao.insertCourse(course)
    }

    suspend fun deleteCourse(courseId: String) = withContext(Dispatchers.IO) {
        coursesCollection.document(courseId).delete().await()
        courseDao.deleteCourseById(courseId)
    }

    // --- DECK DATA ---
    fun getDecksForCourse(courseId: String): LiveData<List<Deck>> {
        return deckDao.getDecksForCourse(courseId)
    }

    suspend fun refreshDecksForCourse(courseId: String) = withContext(Dispatchers.IO) {
        try {
            val snapshot = decksCollection.whereEqualTo("courseId", courseId).get().await()
            val firestoreDecks = snapshot.toObjects(Deck::class.java)
            // Insert all fetched decks. OnConflictStrategy.REPLACE will handle updates.
            firestoreDecks.forEach { deckDao.insertDeck(it) }
            Log.d("StudyRepository", "Decks for course $courseId refreshed successfully.")
        } catch (e: Exception) {
            Log.e("StudyRepository", "Error refreshing decks", e)
        }
    }

    suspend fun addDeck(deck: Deck) = withContext(Dispatchers.IO) {
        decksCollection.document(deck.deckId).set(deck).await()
        deckDao.insertDeck(deck)
    }

    suspend fun renameDeck(deck: Deck) = withContext(Dispatchers.IO) {
        decksCollection.document(deck.deckId).update("deckTitle", deck.deckTitle).await()
        deckDao.updateDeck(deck)
    }

    suspend fun deleteDeck(deck: Deck) = withContext(Dispatchers.IO) {
        decksCollection.document(deck.deckId).delete().await()
        deckDao.deleteDeckById(deck.deckId)
    }

    suspend fun getDeckById(deckId: String): Deck? = withContext(Dispatchers.IO) {
        deckDao.getDeckById(deckId)
    }

    // --- FLASHCARD DATA ---
    fun getFlashcardsForDeck(deckId: String): LiveData<List<Flashcard>> {
        return flashcardDao.getFlashcardsForDeck(deckId)
    }

    fun getFlashcardsForCourse(courseId: String): LiveData<List<Flashcard>> {
        return flashcardDao.getFlashcardsForCourse(courseId)
    }

    suspend fun addFlashcard(flashcard: Flashcard) {
        flashcardsCollection.document(flashcard.flashcardId).set(flashcard).await()
        flashcardDao.insertFlashcard(flashcard)
    }

    suspend fun updateFlashcard(flashcard: Flashcard) {
        flashcardsCollection.document(flashcard.flashcardId).set(flashcard).await() // Use set for simplicity
        flashcardDao.updateFlashcard(flashcard)
    }

    suspend fun deleteFlashcard(flashcard: Flashcard) {
        flashcardsCollection.document(flashcard.flashcardId).delete().await()
        flashcardDao.deleteFlashcardById(flashcard.flashcardId)
    }

    suspend fun getFlashcardsForDeckSync(deckId: String): List<Flashcard> = withContext(Dispatchers.IO) {
        flashcardDao.getFlashcardsForDeckSync(deckId)
    }
}