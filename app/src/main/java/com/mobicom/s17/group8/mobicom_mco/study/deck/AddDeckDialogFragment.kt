package com.mobicom.s17.group8.mobicom_mco.study.deck

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddDeckBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel

class AddDeckDialogFragment : DialogFragment() {

    private lateinit var binding: DialogAddDeckBinding

    private val viewModel: StudyViewModel by activityViewModels()


    // Companion object for safe argument passing
    companion object {
        private const val ARG_COURSE_ID = "course_id"
        fun newInstance(courseId: String): AddDeckDialogFragment {
            val fragment = AddDeckDialogFragment()
            fragment.arguments = Bundle().apply { putString(ARG_COURSE_ID, courseId) }
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddDeckBinding.inflate(layoutInflater)
        val courseId = requireArguments().getString(ARG_COURSE_ID)!!

        val deckTitleLimit = 55
        binding.deckTitleCounter.text = getString(R.string.char_counter, 0, deckTitleLimit)

        // Disable save button initially
        binding.btnSave.isEnabled = false
        binding.btnSave.setTextColor(requireContext().getColor(android.R.color.darker_gray))

        binding.etDeckTitle.addTextChangedListener { text ->
            val length = text?.length ?: 0
            binding.deckTitleCounter.text = getString(R.string.char_counter, length, deckTitleLimit)

            // Enable/disable save button based on input
            val isEnabled = text.toString().trim().isNotEmpty()
            binding.btnSave.isEnabled = isEnabled
            binding.btnSave.setTextColor(
                if (isEnabled) "#5A8392".toColorInt() else requireContext().getColor(android.R.color.darker_gray)
            )
        }

        binding.btnClear.setOnClickListener { binding.etDeckTitle.setText("") }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val deckTitle = binding.etDeckTitle.text.toString().trim()
            viewModel.addDeck(courseId, deckTitle)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Add Deck")
            .setView(binding.root)
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        listOf(binding.btnClear, binding.btnCancel, binding.btnSave).forEach {
            it.background = null
        }
    }
}