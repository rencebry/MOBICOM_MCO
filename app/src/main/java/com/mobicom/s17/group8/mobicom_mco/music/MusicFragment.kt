package com.mobicom.s17.group8.mobicom_mco.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentMusicBinding

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycler view setup
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val musicTracks = getMusicData()
        val musicAdapter = MusicAdapter(musicTracks) { track ->
            if (track != null) {
                Toast.makeText(requireContext(), "Playing ${track.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Playback stopped", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvMusicGrid.apply {
            adapter = musicAdapter
        }
    }

    private fun getMusicData(): List<MusicTrack> {
        // not sure if we need api for this but placeholder muna
        return listOf(
            MusicTrack("Rainfall", R.color.vinyl_blue),
            MusicTrack("Forest", R.color.vinyl_green),
            MusicTrack("Brown Noise", R.color.vinyl_yellow),
            MusicTrack("Fireplace", R.color.vinyl_orange),
            MusicTrack("Lo-fi", R.color.vinyl_purple),
            MusicTrack("Soft Ambient", R.color.vinyl_mint)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}