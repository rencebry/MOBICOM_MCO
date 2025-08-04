package com.mobicom.s17.group8.mobicom_mco.database.study

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<Course>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Query("SELECT * FROM courses WHERE userId = :userId ORDER BY courseTitle ASC")
    fun getCoursesForUser(userId: String): LiveData<List<Course>>

    @Query("DELETE FROM courses WHERE courseId = :courseId")
    suspend fun deleteCourseById(courseId: String)

    @Query("UPDATE courses SET deckCount = :count WHERE courseId = :courseId")
    suspend fun updateDeckCount(courseId: String, count: Int)
}