package com.mobicom.s17.group8.mobicom_mco.study.flashcard

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
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddFlashcardBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel

class AddFlashcardDialogFragment : DialogFragment() {

    private lateinit var binding: DialogAddFlashcardBinding

    private val viewModel: StudyViewModel by activityViewModels()


    companion object {
        private const val ARG_DECK_ID = "deck_id"
        private const val ARG_COURSE_ID = "course_id"

        fun newInstance(deckId: String, courseId: String): AddFlashcardDialogFragment {
            val fragment = AddFlashcardDialogFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_DECK_ID, deckId)
                putString(ARG_COURSE_ID, courseId)
            }
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddFlashcardBinding.inflate(layoutInflater)
        val deckId = requireArguments().getString(ARG_DECK_ID)!!
        val courseId = requireArguments().getString(ARG_COURSE_ID)!!

        val questionLimit = 160
        val answerLimit = 250

        // Initial setup
        binding.questionCounter.text = getString(R.string.char_counter, 0, questionLimit)
        binding.answerCounter.text = getString(R.string.char_counter, 0, answerLimit)
        binding.btnSave.isEnabled = false
        binding.btnSave.setTextColor(requireContext().getColor(android.R.color.darker_gray))


        val updateSaveButtonState = {
            val question = binding.etQuestion.text.toString().trim()
            val answer = binding.etAnswer.text.toString().trim()
            binding.btnSave.isEnabled = question.isNotEmpty() && answer.isNotEmpty()
            binding.btnSave.setTextColor(
                if (binding.btnSave.isEnabled) "#5A8392".toColorInt()
                else requireContext().getColor(android.R.color.darker_gray)
            )
        }

        binding.etQuestion.addTextChangedListener {
            val length = it?.length ?: 0
            binding.questionCounter.text = getString(R.string.char_counter, length, questionLimit)
            updateSaveButtonState()
        }

        binding.etAnswer.addTextChangedListener {
            val length = it?.length ?: 0
            binding.answerCounter.text = getString(R.string.char_counter, length, answerLimit)
            updateSaveButtonState()
        }

        binding.btnClear.setOnClickListener {
            binding.etQuestion.setText("")
            binding.etAnswer.setText("")
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val question = binding.etQuestion.text.toString()
            val answer = binding.etAnswer.text.toString()

            viewModel.addFlashcard(deckId, courseId, question, answer)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Add Flashcard")
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