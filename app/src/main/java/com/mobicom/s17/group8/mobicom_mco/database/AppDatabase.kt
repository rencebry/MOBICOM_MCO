package com.mobicom.s17.group8.mobicom_mco.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Task::class, TaskList::class],
    version = 1,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun taskListDao(): TaskListDao

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
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}