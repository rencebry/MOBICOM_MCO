package com.mobicom.s17.group8.mobicom_mco.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mobicom.s17.group8.mobicom_mco.database.study.CourseDao
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskDao
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskListDao
import com.mobicom.s17.group8.mobicom_mco.database.user.User
import com.mobicom.s17.group8.mobicom_mco.database.user.UserDao
import com.mobicom.s17.group8.mobicom_mco.database.study.Course
import com.mobicom.s17.group8.mobicom_mco.database.study.Deck
import com.mobicom.s17.group8.mobicom_mco.database.study.DeckDao
import com.mobicom.s17.group8.mobicom_mco.database.study.FlashcardDao
import com.mobicom.s17.group8.mobicom_mco.database.study.Flashcard


//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch

@Database(entities = [User::class, Task::class, TaskList::class, Course::class, Deck::class, Flashcard::class],
    version = 2,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun taskListDao(): TaskListDao
    abstract fun courseDao(): CourseDao
    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao

    companion object {
        // Volatile ensures that the value of INSTANCE is always up-to-date
        // and the same to all execution threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // If the INSTANCE is not null, then return it,
            // if it is, then create the database.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    //.fallbackToDestructiveMigration() // NOTE: This will recreate the database if the schema changes, remove once stable
                .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

