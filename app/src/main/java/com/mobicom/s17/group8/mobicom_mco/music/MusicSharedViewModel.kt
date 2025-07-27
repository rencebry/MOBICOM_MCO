package com.mobicom.s17.group8.mobicom_mco.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicSharedViewModel : ViewModel() {

    private val _currentlyPlayingTrack = MutableLiveData<MusicTrack?>()
    val currentlyPlayingTrack: LiveData<MusicTrack?> = _currentlyPlayingTrack

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    // This will hold our connection to the MusicService
    var musicService: MusicService? = null

    fun selectTrack(track: MusicTrack?) {
        _currentlyPlayingTrack.value = track

        if (track != null) {
            // Tell the service to play the new track
            musicService?.playTrack(track.trackResId)
            _isPlaying.value = true
        } else {
            // No track selected, tell the service to stop
            musicService?.stopPlayback()
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        musicService?.togglePlayPause()
        // Update our LiveData to reflect the service's new state
        _isPlaying.value = musicService?.isPlaying()
    }
}