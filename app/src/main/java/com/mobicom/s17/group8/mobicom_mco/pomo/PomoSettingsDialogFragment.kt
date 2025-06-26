package com.mobicom.s17.group8.mobicom_mco.pomo // Use your pomo package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogPomoSettingsBinding

// pop up settings
class PomoSettingsDialogFragment : DialogFragment() {

    private var _binding: DialogPomoSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPomoSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // close button (x)
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            // placeholder for now
            val taskName = binding.etTaskName.text.toString()
            val focusMinutes = binding.etFocusTime.text.toString().toIntOrNull() ?: 30
            val breakMinutes = binding.etBreakTime.text.toString().toIntOrNull() ?: 5
            val restMinutes = binding.etRestTime.text.toString().toIntOrNull() ?: 15

            // TODO: Pass this data back to the PomoViewModel
            // another shared view model?? for live update

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