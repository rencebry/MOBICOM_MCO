package com.mobicom.s17.group8.mobicom_mco.database.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    // Insert or update a user. If a user with the same UID already exists, it will be replaced.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    // Get a specific user by their UID. Returns LiveData, so your UI can automatically update.
    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    fun getUserById(uid: String): LiveData<User>

    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    suspend fun getSingleUserById(uid: String): User?

    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    suspend fun getNonLiveUserById(uid: String): User?
}