package com.mobicom.s17.group8.mobicom_mco.sync

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SyncPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SYNC_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val LAST_SYNC_TIMESTAMP_KEY = "last_sync_timestamp"
    }

    fun getLastSyncTimestamp(userId: String): String? {
        return prefs.getString("${LAST_SYNC_TIMESTAMP_KEY}_$userId", null)
    }

    fun updateLastSyncTimestamp(userId: String, timestamp: String) {
        prefs.edit { putString("${LAST_SYNC_TIMESTAMP_KEY}_$userId", timestamp) }
    }
}