package com.mobicom.s17.group8.mobicom_mco.study

import androidx.annotation.ColorRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mobicom.s17.group8.mobicom_mco.database.study.Course
import com.mobicom.s17.group8.mobicom_mco.database.study.Deck
import com.mobicom.s17.group8.mobicom_mco.database.study.Flashcard
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StudyViewModel(
    private val repository: StudyRepository,
    private val userId: String
) : ViewModel() {

    // --- COURSES DATA (from Repository) ---
    val courses: LiveData<List<Course>> = repository.allCourses

    private val _isEditMode = MutableLiveData(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    private val _allDecks = MutableLiveData<List<Deck>>()
    private val _allFlashcards = MutableLiveData<List<Flashcard>>()

    init {
        viewModelScope.launch {
            repository.refreshCourses()
        }
    }

    // --- COURSE FUNCTIONS (interact with Repository) ---
    fun toggleEditMode() {
        _isEditMode.value = !(_isEditMode.value ?: false)
    }

    fun addCourse(courseTitle: String, @ColorRes colorResId: Int) {
        viewModelScope.launch {
            val newCourse = Course(
                courseId = UUID.randomUUID().toString(),
                courseTitle = courseTitle,
                deckCount = 0,
                colorResId = colorResId,
                userId = userId
            )
            repository.addCourse(newCourse)
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            repository.deleteCourse(courseId)
        }
    }

    suspend fun getDeckById(deckId: String): Deck? {
        return repository.getDeckById(deckId)
    }

    fun addDeck(courseId: String, deckTitle: String) {
        viewModelScope.launch {
            val newDeck = Deck(
                deckId = UUID.randomUUID().toString(),
                courseId = courseId,
                deckTitle = deckTitle,
                dateCreated = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                cardCount = 0,
                userId = userId
            )
            repository.addDeck(newDeck)
        }
    }

    fun renameDeck(deck: Deck, newTitle: String) {
        viewModelScope.launch {
            val updatedDeck = deck.copy(deckTitle = newTitle)
            repository.renameDeck(updatedDeck)
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
        }
    }



    fun getDecksForCourse(courseId: String): LiveData<List<Deck>> {
        viewModelScope.launch {
            repository.refreshDecksForCourse(courseId)
        }
        return repository.getDecksForCourse(courseId)
    }

    fun getFlashcardsForDeck(deckId: String): LiveData<List<Flashcard>> {
        // TODO: Add a refreshFlashcards() call here if you want to sync from Firestore
        return repository.getFlashcardsForDeck(deckId)
    }

    fun getFlashcardsForCourse(courseId: String): LiveData<List<Flashcard>> {
        return repository.getFlashcardsForCourse(courseId)
    }

    fun addFlashcard(deckId: String, courseId: String, question: String, answer: String) {
        viewModelScope.launch {
            val newFlashcard = Flashcard(
                flashcardId = UUID.randomUUID().toString(),
                deckId = deckId,
                courseId = courseId,
                question = question,
                answer = answer
            )
            repository.addFlashcard(newFlashcard)
            // TODO: Also update the cardCount in the Deck object
        }
    }

    fun updateFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.updateFlashcard(flashcard)
        }
    }



    fun deleteFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.deleteFlashcard(flashcard)
            // TODO: Also update the cardCount in the Deck object
        }
    }

    fun getFlashcardById(flashcardId: String): Flashcard? {
        return _allFlashcards.value?.find { it.flashcardId == flashcardId }
    }


    fun updateDeck(deck: Deck) {
        viewModelScope.launch {
            repository.renameDeck(deck) // renameDeck in repo can be a general purpose update
        }
    }

    suspend fun getFlashcardsForExport(deckId: String): List<Flashcard> {
        return repository.getFlashcardsForDeckSync(deckId)
    }
}

class StudyViewModelFactory(
    private val repository: StudyRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudyViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}