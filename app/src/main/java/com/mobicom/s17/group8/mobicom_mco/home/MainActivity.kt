package com.mobicom.s17.group8.mobicom_mco.home

import android.util.Log
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.content.Intent
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mobicom.s17.group8.mobicom_mco.R
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.databinding.ActivityMainBinding
import com.mobicom.s17.group8.mobicom_mco.music.MusicService
import com.mobicom.s17.group8.mobicom_mco.music.MusicSharedViewModel
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.api.TasksApiService
import com.mobicom.s17.group8.mobicom_mco.auth.UserAuthViewModel
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskRepository
import com.mobicom.s17.group8.mobicom_mco.sync.SyncPrefs
import com.mobicom.s17.group8.mobicom_mco.sync.SyncRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val musicSharedViewModel: MusicSharedViewModel by lazy {
        ViewModelProvider(this).get(MusicSharedViewModel::class.java)
    }

    private val userAuthViewModel: UserAuthViewModel by viewModels()

    private lateinit var syncPrefs: SyncPrefs

    private var musicService: MusicService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()

            musicSharedViewModel.musicService = musicService
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            musicSharedViewModel.musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        syncPrefs = SyncPrefs(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Firebase.auth.currentUser?.let { user ->
            userAuthViewModel.setCurrentUser(user.uid)
        }

        lifecycleScope.launch{
            userAuthViewModel.currentUserId.collect { userId ->
                if (userId != null) {
                    performFullSync(userId)
                }
            }
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // bottom navigation
        binding.bottomNavigation.setupWithNavController(navController)
        binding.musicPlayerWidget.widgetPauseButton.setOnClickListener {
            musicSharedViewModel.togglePlayPause()
        }
        observeMusicPlayer()

        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    private fun observeMusicPlayer() {
        musicSharedViewModel.currentlyPlayingTrack.observe(this) { track ->
            if (track != null) {
                // track is selected: show widget
                binding.musicWidgetContainer.visibility = View.VISIBLE
                // update content
                binding.musicPlayerWidget.widgetTrackName.text = "♪ ${track.name}"

                // color in the middle
                val color = ContextCompat.getColor(this, track.centerColorResId)
                binding.musicPlayerWidget.root.background.setTint(color)
            } else {
                // hide widget
                binding.musicWidgetContainer.visibility = View.GONE
            }
        }

        musicSharedViewModel.isPlaying.observe(this) { playing ->
            if (playing) {
                binding.musicPlayerWidget.widgetPauseButton.setImageResource(R.drawable.pause)
            } else {
                binding.musicPlayerWidget.widgetPauseButton.setImageResource(R.drawable.play)
            }
        }
    }

    private fun performFullSync(userId: String) {
        val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleAccount == null) {
            Log.e("MainActivity", "Cannot sync, Google account is null.")
            return
        }

        lifecycleScope.launch {
            Log.d("MainActivity", "Starting full sync for user $userId")

            val lastSyncTimestamp = syncPrefs.getLastSyncTimestamp(userId)
            val apiService = TasksApiService(applicationContext, googleAccount)
            val database = AppDatabase.getDatabase(applicationContext)
            val taskRepository = TaskRepository(database.taskDao(), database.taskListDao())
            val syncRepository = SyncRepository(apiService, taskRepository, userId)

            try {
                val newSyncTimestamp = syncRepository.performSync(lastSyncTimestamp)

                syncPrefs.updateLastSyncTimestamp(userId, newSyncTimestamp)
                Log.d("MainActivity", "Full sync successful. New timestamp saved.")
            } catch (e: Exception) {
                Log.e("MainActivity", "Full sync failed", e)
            }
        }
    }
}