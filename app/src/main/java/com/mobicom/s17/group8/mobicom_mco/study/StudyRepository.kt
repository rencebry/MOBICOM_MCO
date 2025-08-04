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

    suspend fun refreshFlashcardsForDeck(deckId: String) = withContext(Dispatchers.IO) {
        try {
            val snapshot = flashcardsCollection.whereEqualTo("deckId", deckId).get().await()
            val firestoreFlashcards = snapshot.toObjects(Flashcard::class.java)
            flashcardDao.insertAll(firestoreFlashcards)
            Log.d("StudyRepository", "Flashcards for deck $deckId refreshed.")
        } catch (e: Exception) {
            Log.e("StudyRepository", "Error refreshing flashcards", e)
        }
    }

    suspend fun addDeck(deck: Deck) = withContext(Dispatchers.IO) {
        decksCollection.document(deck.deckId).set(deck).await()
        deckDao.insertDeck(deck)

        updateCourseDeckCount(deck.courseId)
    }

    suspend fun renameDeck(deck: Deck) = withContext(Dispatchers.IO) {
        decksCollection.document(deck.deckId).update("deckTitle", deck.deckTitle).await()
        deckDao.updateDeck(deck)
    }

    suspend fun deleteDeck(deck: Deck) = withContext(Dispatchers.IO) {
        decksCollection.document(deck.deckId).delete().await()
        deckDao.deleteDeckById(deck.deckId)

        updateCourseDeckCount(deck.courseId)
    }

    suspend fun getDeckById(deckId: String): Deck? = withContext(Dispatchers.IO) {
        deckDao.getDeckById(deckId)
    }

    // --- FLASHCARD DATA ---
    fun getFlashcardsForDeck(deckId: String): LiveData<List<Flashcard>> {
        Log.d("StudyRepository", "Getting LiveData for flashcards of deck: $deckId")
        return flashcardDao.getFlashcardsForDeck(deckId)
    }

    fun getFlashcardsForCourse(courseId: String): LiveData<List<Flashcard>> {
        Log.d("StudyRepository", "Getting LiveData for flashcards of course: $courseId")
        return flashcardDao.getFlashcardsForCourse(courseId)
    }

    suspend fun getFlashcardsForDeckFromFirestore(deckId: String): List<Flashcard> = withContext(Dispatchers.IO) {
        try {
            val snapshot = flashcardsCollection.whereEqualTo("deckId", deckId).get().await()
            return@withContext snapshot.toObjects(Flashcard::class.java)
        } catch (e: Exception) {
            Log.e("StudyRepository", "Error fetching flashcards from Firestore", e)
            return@withContext emptyList() // Return an empty list on error
        }
    }


    suspend fun addFlashcard(flashcard: Flashcard) = withContext(Dispatchers.IO) {
        flashcardsCollection.document(flashcard.flashcardId).set(flashcard).await()
        try {
            flashcardDao.insertFlashcard(flashcard)
        } catch (e: Exception) {
            Log.e("StudyRepository", "Failed to cache flashcard in Room, but it's saved in Firestore.", e)
        }
        updateDeckCardCount(flashcard.deckId)
    }

    suspend fun insertFlashcard(flashcard: Flashcard) {
        flashcardsCollection.document(flashcard.flashcardId).set(flashcard).await()
        flashcardDao.insertFlashcard(flashcard)
    }

    suspend fun updateFlashcard(flashcard: Flashcard) = withContext(Dispatchers.IO) {
        flashcardsCollection.document(flashcard.flashcardId).set(flashcard).await()
        flashcardDao.updateFlashcard(flashcard)
    }

    suspend fun deleteFlashcard(flashcard: Flashcard) = withContext(Dispatchers.IO) {
        flashcardsCollection.document(flashcard.flashcardId).delete().await()
        flashcardDao.deleteFlashcardById(flashcard.flashcardId)
        updateDeckCardCount(flashcard.deckId)
    }

    suspend fun getFlashcardsForDeckSync(deckId: String): List<Flashcard> = withContext(Dispatchers.IO) {
        try {
            val flashcards = flashcardDao.getFlashcardsForDeckSync(deckId)
            Log.d("StudyRepository", "Sync fetch: ${flashcards.size} flashcards for deck $deckId")
            flashcards
        } catch (e: Exception) {
            Log.e("StudyRepository", "Error getting flashcards for deck sync $deckId: ${e.message}", e)
            emptyList()
        }
    }

    private suspend fun updateCourseDeckCount(courseId: String) {
        try {
            val count = deckDao.getDeckCountForCourse(courseId)
            coursesCollection.document(courseId).update("deckCount", count).await()
            courseDao.updateDeckCount(courseId, count)
            Log.d("StudyRepository", "Course $courseId deck count updated to $count")
        } catch (e: Exception) {
            Log.e("StudyRepository", "Error updating deck count for course $courseId: ${e.message}", e)
        }
    }

    private suspend fun updateDeckCardCount(deckId: String) {
        try {
            val count = flashcardDao.getFlashcardCountForDeck(deckId)
            decksCollection.document(deckId).update("cardCount", count).await()
            deckDao.updateCardCount(deckId, count)
            Log.d("StudyRepository", "Deck $deckId card count updated to $count")
        } catch (e: Exception) {
            Log.e("StudyRepository", "Error updating card count for deck $deckId: ${e.message}", e)
        }
    }

    suspend fun getDecksForCourseSync(courseId: String): List<Deck> = withContext(Dispatchers.IO) {
        try {
            val decks = deckDao.getDecksForCourseSync(courseId)
            Log.d("StudyRepository", "Sync fetch: ${decks.size} decks for course $courseId")
            decks
        } catch (e: Exception) {
            Log.e("StudyRepository", "Error getting decks for course sync $courseId: ${e.message}", e)
            emptyList()
        }
    }
}