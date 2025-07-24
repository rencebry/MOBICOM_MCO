package com.mobicom.s17.group8.mobicom_mco.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//class DatabaseProvider {
//}

fun provideDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_database"
    )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = AppDatabase.getDatabase(context)
                    val defaultTaskList = TaskList(
                        id = "default",
                        title = "My Tasks",
                        updated = System.currentTimeMillis().toString(),
                        isSynced = false,
                        isDeleted = false,
                        isDeletable = false // Default task list should not be deletable
                    )
                    database.taskListDao().insertTaskList(defaultTaskList)
                }
            }
        })
        .build()
}