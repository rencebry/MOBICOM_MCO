package com.mobicom.s17.group8.mobicom_mco.music

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()
    var currentTrackResId: Int? = null
        private set

    inner class MusicBinder : Binder() {
        // Return this instance of MusicService so clients can call public methods
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun playTrack(trackResId: Int) {
        if (currentTrackResId == trackResId && mediaPlayer?.isPlaying == true) {
            return // Don't restart the same track if it's already playing
        }

        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(this, trackResId).apply {
            isLooping = true // Perfect for ambient sounds
            start()
        }
        currentTrackResId = trackResId
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.start()
            }
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrackResId = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
    }
}