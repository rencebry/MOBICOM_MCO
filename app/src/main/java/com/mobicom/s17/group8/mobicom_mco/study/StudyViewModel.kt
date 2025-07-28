package com.mobicom.s17.group8.mobicom_mco.study

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobicom.s17.group8.mobicom_mco.R
import java.util.UUID

class StudyViewModel : ViewModel() {

    // --- COURSES DATA ---
    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    private val _isEditMode = MutableLiveData(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    // --- DECKS & FLASHCARDS DATA (using placeholders for now) ---
    private val allDecks = MutableLiveData<List<Deck>>()
    private val allFlashcards = MutableLiveData<List<Flashcard>>()


    init {
        loadPlaceholders()
    }

    // --- Public Functions to be called by Fragments ---

    fun getDecksForCourse(courseId: String): LiveData<List<Deck>> {
        val filteredDecks = allDecks.value?.filter { it.courseId == courseId }
        return MutableLiveData(filteredDecks)
    }

    fun getFlashcardsForDeck(deckId: String): LiveData<List<Flashcard>> {
        val filteredFlashcards = allFlashcards.value?.filter { it.deckId == deckId }
        return MutableLiveData(filteredFlashcards)
    }

    fun toggleEditMode() {
        _isEditMode.value = !(_isEditMode.value ?: false)
    }

    fun addCourse(course: Course) {
        val currentList = _courses.value?.toMutableList() ?: mutableListOf()
        currentList.add(course)
        _courses.value = currentList
    }

    fun deleteCourse(courseId: String) {
        val currentList = _courses.value?.toMutableList() ?: return
        currentList.removeAll { it.id == courseId }
        _courses.value = currentList
    }

    // --- placeholders ---
    private fun loadPlaceholders() {
        _courses.value = listOf(
            Course("course1", "STCLOUD", 10, R.color.vinyl_blue),
            Course("course2", "MOBICOM", 8, R.color.vinyl_green),
            Course("course3", "CSOPESY", 12, R.color.vinyl_yellow)
        )
        allDecks.value = listOf(
            Deck("course1", "deck1", "Android Basics", 10, "2024-07-17"),
            Deck("course1", "deck2", "Kotlin Fundamentals", 8, "2024-07-16"),
            Deck("course2", "deck3", "Jetpack Compose", 12, "2024-07-15")
        )
        allFlashcards.value = listOf(
            Flashcard("1", "course1", "deck1", "What is Android?", "An OS."),
            Flashcard("2", "course1", "deck1", "What is Kotlin?", "A modern language."),
            Flashcard("3", "course2", "deck3", "What is a Fragment?", "A modular UI block.")
        )
    }
}