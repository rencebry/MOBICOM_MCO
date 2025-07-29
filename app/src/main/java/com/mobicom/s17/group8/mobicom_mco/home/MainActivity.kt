package com.mobicom.s17.group8.mobicom_mco.home

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.auth.UserAuthViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val musicSharedViewModel: MusicSharedViewModel by lazy {
        ViewModelProvider(this).get(MusicSharedViewModel::class.java)
    }

    private val userAuthViewModel: UserAuthViewModel by viewModels()

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.auth.currentUser?.let { user ->
            userAuthViewModel.setCurrentUser(user.uid)
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
}