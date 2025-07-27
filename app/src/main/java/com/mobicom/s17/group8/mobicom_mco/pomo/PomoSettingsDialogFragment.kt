package com.mobicom.s17.group8.mobicom_mco.pomo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogPomoSettingsBinding

class PomoSettingsDialogFragment : DialogFragment() {

    private var _binding: DialogPomoSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PomoViewModel by lazy {
        ViewModelProvider(requireActivity()).get(PomoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogPomoSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val taskName = binding.etTaskName.text.toString()
            val focusMinutes = binding.etFocusTime.text.toString().toIntOrNull() ?: 25
            val breakMinutes = binding.etBreakTime.text.toString().toIntOrNull() ?: 5
            val restMinutes = binding.etRestTime.text.toString().toIntOrNull() ?: 15
            val sessions = binding.etSessionsCount.text.toString().toIntOrNull() ?: 4

            viewModel.updateSettings(taskName, focusMinutes, breakMinutes, restMinutes, sessions)

            dismiss()
        }
    }

    // lessen the opacity of the bg when pop up settings show up
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}