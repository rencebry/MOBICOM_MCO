package com.mobicom.s17.group8.mobicom_mco.pomo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentPomoBinding

class PomoFragment : Fragment() {

    private var _binding: FragmentPomoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PomoViewModel by lazy {
        ViewModelProvider(this).get(PomoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPomoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.timeLeftInMillis.observe(viewLifecycleOwner) { millis ->
            val minutes = (millis / 1000) / 60
            val seconds = (millis / 1000) % 60

            // set time
            binding.circularTimer.setTime(minutes, seconds)

            // update progress
//             val progress = millis.toFloat() / (25 * 60 * 1000L)
//             binding.circularTimer.setProgress(progress)
        }

        // start and stop buttons
        binding.btnStopStart.setOnClickListener {
            viewModel.toggleTimer()
        }

        // reset button
        binding.btnReset.setOnClickListener {
            viewModel.resetTimer()
        }

        // settings
        binding.btnPomoSettings.setOnClickListener {
            PomoSettingsDialogFragment().show(parentFragmentManager, "PomoSettingsDialog")
        }

        observeViewModel()

    }

    private fun observeViewModel() {
        // observe time remaining
        viewModel.timeLeftInMillis.observe(viewLifecycleOwner) { millis ->
            val minutes = (millis / 1000) / 60
            val seconds = (millis / 1000) % 60
            binding.circularTimer.setTime(minutes, seconds)
            // to update progress
        }

        // running state
        viewModel.isTimerRunning.observe(viewLifecycleOwner) { isRunning ->
            if (isRunning) {
                // timer is running: show stop and change color
                binding.btnStopStart.text = "Stop"

                // change color button
                binding.btnStopStart.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.button_stop_color)
            } else {
                // timer is stopped/paused: show start
                binding.btnStopStart.text = "Start"

                // back to start button
                binding.btnStopStart.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.button_start_color)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}