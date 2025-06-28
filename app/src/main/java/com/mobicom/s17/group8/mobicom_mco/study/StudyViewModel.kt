package com.mobicom.s17.group8.mobicom_mco.study

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobicom.s17.group8.mobicom_mco.R
import java.util.UUID

class StudyViewModel : ViewModel() {

    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    // user clicks the edit button
    private val _isEditMode = MutableLiveData<Boolean>(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    init {
        loadCourses()
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

    // only placeholders
    private fun loadCourses() {
        _courses.value = listOf(
            Course(
                id = UUID.randomUUID().toString(),
                name = "STCLOUD",
                deckCount = 10,
                colorResId = R.color.vinyl_blue
            ),
            Course(
                id = UUID.randomUUID().toString(),
                name = "MOBICOM",
                deckCount = 8,
                colorResId = R.color.vinyl_green
            ),
            Course(
                id = UUID.randomUUID().toString(),
                name = "CSOPESY",
                deckCount = 12,
                colorResId = R.color.vinyl_yellow
            ),
            Course(
                id = UUID.randomUUID().toString(),
                name = "LCFILIB",
                deckCount = 5,
                colorResId = R.color.vinyl_orange
            ),
            Course(
                id = UUID.randomUUID().toString(),
                name = "CSARCH2",
                deckCount = 7,
                colorResId = R.color.vinyl_purple
            ),
            Course(
                id = UUID.randomUUID().toString(),
                name = "CSARCH1",
                deckCount = 9,
                colorResId = R.color.vinyl_mint
            )
        )
    }
}