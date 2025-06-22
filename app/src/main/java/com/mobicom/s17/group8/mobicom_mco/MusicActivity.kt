package com.mobicom.s17.group8.mobicom_mco

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobicom.s17.group8.mobicom_mco.databinding.ActivityMusicBinding

class MusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val musicTracks = getMusicData()
        val musicAdapter = MusicAdapter(musicTracks) { track ->
            Toast.makeText(
                this,
                "Playing ${track.name}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.rvMusicGrid.apply {
            adapter = musicAdapter
        }
    }

    private fun getMusicData(): List<MusicTrack> {
        return listOf(
            MusicTrack("Rainfall", R.color.vinyl_blue),
            MusicTrack("Forest", R.color.vinyl_green),
            MusicTrack("Brown Noise", R.color.vinyl_yellow),
            MusicTrack("Fireplace", R.color.vinyl_orange),
            MusicTrack("Lo-fi", R.color.vinyl_purple),
            MusicTrack("Soft Ambient", R.color.vinyl_mint)
        )
    }
}