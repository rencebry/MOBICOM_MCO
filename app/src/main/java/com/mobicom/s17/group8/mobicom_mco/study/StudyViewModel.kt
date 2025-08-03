package com.mobicom.s17.group8.mobicom_mco.study

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobicom.s17.group8.mobicom_mco.R
import java.util.UUID
import androidx.lifecycle.map

class StudyViewModel : ViewModel() {

    // --- COURSES DATA ---
    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    private val _isEditMode = MutableLiveData(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    // --- DECKS & FLASHCARDS DATA (using placeholders for now) ---
    private val allDecks = MutableLiveData<List<Deck>>()
    private val allFlashcards = MutableLiveData<List<Flashcard>>()

    private val _tempFlashcards = MutableLiveData<List<Flashcard>>()
    val tempFlashcards: LiveData<List<Flashcard>> get() = _tempFlashcards

    fun setTempFlashcards(flashcards: List<Flashcard>) {
        _tempFlashcards.value = flashcards
    }

    init {
        if (_courses.value.isNullOrEmpty()) {
            loadPlaceholders()
        }
    }

    // --- Public Functions to be called by Fragments ---
    fun getDecksForCourse(courseId: String): LiveData<List<Deck>> =
        allDecks.map { deckList ->
            deckList.filter { it.courseId == courseId }
        }

    fun getFlashcardsForDeck(deckId: String): LiveData<List<Flashcard>> {
        return allFlashcards.map { list ->
            list.filter { it.deckId == deckId }
        }
    }

    fun getFlashcardsForDeckSync(deckId: String): List<Flashcard> {
        return allFlashcards.value?.filter { it.deckId == deckId } ?: emptyList()
    }

    fun getCourseIdFromDeck(deckId: String): String {
        return allDecks.value?.find { it.deckId == deckId }?.courseId.orEmpty()
    }

    fun toggleEditMode() {
        _isEditMode.value = !(_isEditMode.value ?: false)
    }

    // Course functions
    fun addCourse(course: Course) {
        val currentList = _courses.value?.toMutableList() ?: mutableListOf()
        currentList.add(course)
        _courses.value = currentList
    }

    fun deleteCourse(courseId: String) {
        val currentList = _courses.value?.toMutableList() ?: return
        currentList.removeAll { it.courseId == courseId }
        _courses.value = currentList
    }

    // Deck functions
    // TO-DO: Implement DeckDao etc.
    fun addDeck(deck: Deck) {
        val currentList = allDecks.value?.toMutableList() ?: mutableListOf()
        currentList.add(deck)
        allDecks.value = currentList
    }

    fun renameDeck(renamedDeck: Deck) {
        val currentDecks = allDecks.value?.toMutableList() ?: return
        val index = currentDecks.indexOfFirst { it.deckId == renamedDeck.deckId }
        if (index != -1) {
            currentDecks[index] = renamedDeck
            allDecks.value = currentDecks
        }
    }

    fun deleteDeck(deck: Deck) {
        val currentDecks = allDecks.value?.toMutableList() ?: return
        currentDecks.removeAll { it.deckId == deck.deckId }
        allDecks.value = currentDecks

        // Also remove all flashcards in this deck
        val currentFlashcards = allFlashcards.value?.toMutableList() ?: return
        currentFlashcards.removeAll { it.deckId == deck.deckId }
        allFlashcards.value = currentFlashcards
    }

    // Flashcard functions
    fun addFlashcard(deckId: String, newFlashcard: Flashcard) {
        val currentFlashcards = allFlashcards.value?.toMutableList() ?: mutableListOf()
        val currentDecks = allDecks.value?.toMutableList() ?: mutableListOf()

        // Find the deck
        val deckIndex = currentDecks.indexOfFirst { it.deckId == deckId }
        if (deckIndex == -1) return

        val oldDeck = currentDecks[deckIndex]
        val updatedDeck = oldDeck.copy(cardCount = oldDeck.cardCount + 1)
        currentDecks[deckIndex] = updatedDeck

        // Update decks LiveData
        allDecks.value = currentDecks

        // Add flashcard
        val updatedFlashcard = newFlashcard.copy(deckId = deckId, courseId = oldDeck.courseId)
        currentFlashcards.add(updatedFlashcard)
        allFlashcards.value = currentFlashcards
    }

    fun updateFlashcard(updated: Flashcard) {
        val current = allFlashcards.value?.toMutableList() ?: return
        val index = current.indexOfFirst { it.flashcardId == updated.flashcardId }
        if (index != -1) {
            current[index] = updated
            allFlashcards.value = current
        }
    }

    fun deleteFlashcard(flashcard: Flashcard) {
        val currentList = allFlashcards.value ?: return
        allFlashcards.value = currentList.filter { it.flashcardId != flashcard.flashcardId }
    }

    // --- deck and flashcards placeholders ---
    private fun loadPlaceholders() {
        // --- COURSES ---
        val courseStcloud = Course(UUID.randomUUID().toString(), "STCLOUD", 3, R.color.vinyl_blue)
        val courseMobicom = Course(UUID.randomUUID().toString(), "MOBICOM", 3, R.color.vinyl_green)
        val courseCsopesy = Course(UUID.randomUUID().toString(), "CSOPESY", 3, R.color.vinyl_yellow)

        _courses.value = listOf(courseStcloud, courseMobicom, courseCsopesy)

        // --- DECKS ---
        val stcloudDecks = listOf(
            Deck(UUID.randomUUID().toString(), courseStcloud.courseId, "Virtualization Basics", "2024-07-17", 0),
            Deck(UUID.randomUUID().toString(), courseStcloud.courseId, "Cloud Storage & Networking", "2024-07-16", 0),
            Deck(UUID.randomUUID().toString(), courseStcloud.courseId, "Intro to Kubernetes", "2024-07-15", 0)
        )

        val mobicomDecks = listOf(
            Deck(UUID.randomUUID().toString(), courseMobicom.courseId, "Android Architecture", "2024-07-14", 3),
            Deck(UUID.randomUUID().toString(), courseMobicom.courseId, "Mobile App Lifecycle", "2024-07-13", 0),
            Deck(UUID.randomUUID().toString(), courseMobicom.courseId, "Wireless Protocols", "2024-07-12", 0)
        )

        val csopesyDecks = listOf(
            Deck(UUID.randomUUID().toString(), courseCsopesy.courseId, "CPU Scheduling Algorithms", "2024-07-11", 0),
            Deck(UUID.randomUUID().toString(), courseCsopesy.courseId, "Memory Management", "2024-07-10", 0),
            Deck(UUID.randomUUID().toString(), courseCsopesy.courseId, "File Systems and I/O", "2024-07-09", 0)
        )

        val allDeckList = stcloudDecks + mobicomDecks + csopesyDecks
        allDecks.value = allDeckList

        // --- FLASHCARDS for Android Architecture in MOBICOM ---
        allFlashcards.value = listOf(
            Flashcard(
                UUID.randomUUID().toString(),
                mobicomDecks[0].deckId,
                courseMobicom.courseId,
                "What is Android?",
                "An OS."
            ),
            Flashcard(
                UUID.randomUUID().toString(),
                mobicomDecks[0].deckId,
                courseMobicom.courseId,
                "What is Kotlin?",
                "A modern language."
            ),
            Flashcard(
                UUID.randomUUID().toString(),
                mobicomDecks[0].deckId,
                courseMobicom.courseId,
                "What is a Fragment?",
                "A modular UI block."
            )
        )
    }
}
