package com.mobicom.s17.group8.mobicom_mco.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicSharedViewModel : ViewModel() {

    private val _currentlyPlayingTrack = MutableLiveData<MusicTrack?>()
    val currentlyPlayingTrack: LiveData<MusicTrack?> = _currentlyPlayingTrack


    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    fun selectTrack(track: MusicTrack?) {
        if (track == null) {
            // track is deselected
            _isPlaying.value = false // pause
        } else {
            // new track selected
            _isPlaying.value = true // playing
        }
        _currentlyPlayingTrack.value = track
    }

    fun togglePlayPause() {
        val currentlyPlaying = _isPlaying.value ?: false
        _isPlaying.value = !currentlyPlaying
    }
}