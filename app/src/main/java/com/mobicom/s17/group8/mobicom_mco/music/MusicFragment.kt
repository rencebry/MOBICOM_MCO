package com.mobicom.s17.group8.mobicom_mco.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentMusicBinding

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MusicSharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MusicSharedViewModel::class.java)
    }

    private lateinit var musicAdapter: MusicAdapter
    private val musicTracks = getMusicData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        musicAdapter = MusicAdapter(musicTracks) { track ->
            sharedViewModel.selectTrack(track)
        }

        binding.rvMusicGrid.apply {
            adapter = musicAdapter
        }
    }

    private fun observeViewModel() {
        sharedViewModel.currentlyPlayingTrack.observe(viewLifecycleOwner) { currentlyPlayingTrack ->
            val isPlaying = sharedViewModel.isPlaying.value ?: false

            val selectedPosition = if (currentlyPlayingTrack != null) {
                musicTracks.indexOf(currentlyPlayingTrack)
            } else {
                -1
            }

            musicAdapter.setPlaybackState(selectedPosition, isPlaying)
        }


        sharedViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            val currentlyPlayingTrack = sharedViewModel.currentlyPlayingTrack.value
            val selectedPosition = if (currentlyPlayingTrack != null) {
                musicTracks.indexOf(currentlyPlayingTrack)
            } else {
                -1
            }

            musicAdapter.setPlaybackState(selectedPosition, isPlaying)
        }
    }

    private fun getMusicData(): List<MusicTrack> {
        return listOf(
            MusicTrack("Rainfall", R.color.vinyl_blue, R.raw.rainfall),
            MusicTrack("Forest", R.color.vinyl_green, R.raw.forest),
            MusicTrack("Brown Noise", R.color.vinyl_yellow, R.raw.brown_nosie),
            MusicTrack("Fireplace", R.color.vinyl_orange, R.raw.fireplace),
            MusicTrack("Ocean Waves", R.color.vinyl_purple, R.raw.ocean_waves),
            MusicTrack("Birds", R.color.vinyl_mint, R.raw.birds)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}