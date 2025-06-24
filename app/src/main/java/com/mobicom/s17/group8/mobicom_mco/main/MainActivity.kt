package com.mobicom.s17.group8.mobicom_mco.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mobicom.s17.group8.mobicom_mco.R
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.databinding.ActivityMainBinding
import com.mobicom.s17.group8.mobicom_mco.music.MusicSharedViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: MusicSharedViewModel by lazy {
        ViewModelProvider(this).get(MusicSharedViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // bottom navigation
        binding.bottomNavigation.setupWithNavController(navController)
        binding.musicPlayerWidget.widgetPauseButton.setOnClickListener {
            sharedViewModel.togglePlayPause()
        }
        observeMusicPlayer()

    }

    private fun observeMusicPlayer() {
        sharedViewModel.currentlyPlayingTrack.observe(this) { track ->
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

        sharedViewModel.isPlaying.observe(this) { playing ->
            if (playing) {
                binding.musicPlayerWidget.widgetPauseButton.setImageResource(R.drawable.pause)
            } else {
                binding.musicPlayerWidget.widgetPauseButton.setImageResource(R.drawable.play)
            }
        }
    }
}