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
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.mobicom.s17.group8.mobicom_mco.utils.NotificationHelper

class PomoFragment : Fragment() {

    private var _binding: FragmentPomoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PomoViewModel by lazy {
        ViewModelProvider(requireActivity()).get(PomoViewModel::class.java)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. You can now send notifications.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied.
                Toast.makeText(requireContext(), "Notifications will not be shown.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPomoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NotificationHelper.createNotificationChannel(requireContext())
        askForNotificationPermission()

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnStopStart.setOnClickListener {
            viewModel.toggleTimer()
        }
        binding.btnReset.setOnClickListener {
            viewModel.resetTimer()
        }
        binding.btnPomoSettings.setOnClickListener {
            PomoSettingsDialogFragment().show(parentFragmentManager, "PomoSettingsDialog")
        }
    }

    private fun observeViewModel() {
        viewModel.timeLeftInMillis.observe(viewLifecycleOwner) { millis ->
            val minutes = (millis / 1000) / 60
            val seconds = (millis / 1000) % 60
            binding.circularTimer.setTime(minutes, seconds)
        }

        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding.circularTimer.setProgress(progress)
        }

        viewModel.isTimerRunning.observe(viewLifecycleOwner) { isRunning ->
            if (isRunning) {
                binding.btnStopStart.text = "Pause"
                binding.btnStopStart.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.button_stop_color)
            } else {
                binding.btnStopStart.text = "Start"
                binding.btnStopStart.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.button_start_color)
            }
        }

        viewModel.taskName.observe(viewLifecycleOwner) { taskName ->
            if (taskName != null) {
                binding.tvTaskName.text = taskName
                binding.tvTaskName.visibility = View.VISIBLE
            } else {
                binding.tvTaskName.visibility = View.GONE
            }
        }

        viewModel.notificationEvent.observe(viewLifecycleOwner) { event ->
            event?.let { (title, message) ->
                NotificationHelper.sendNotification(requireContext(), title, message)
                viewModel.notificationShown()
            }
        }

        viewModel.timerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                PomoViewModel.TimerState.FOCUS -> {
                    binding.pomoRootLayout.setBackgroundResource(R.drawable.gradient_background_focus)
                    binding.tvStatus.text = "Focus"
                    binding.circularTimer.setProgressColor(R.color.timer_progress_dark)
                }
                PomoViewModel.TimerState.SHORT_BREAK -> {
                    binding.pomoRootLayout.setBackgroundResource(R.drawable.gradient_background_short_break)
                    binding.tvStatus.text = "Take a Break!"
                    binding.circularTimer.setProgressColor(R.color.timer_progress_dark)
                }
                PomoViewModel.TimerState.LONG_BREAK -> {
                    binding.pomoRootLayout.setBackgroundResource(R.drawable.gradient_background_long_break)
                    binding.tvStatus.text = "Rest"
                    binding.circularTimer.setProgressColor(R.color.timer_progress_dark)
                }

                null -> {

                }
            }
        }
    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}