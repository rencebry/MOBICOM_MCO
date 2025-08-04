package com.mobicom.s17.group8.mobicom_mco.study.flashcard

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.study.Flashcard
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogEditFlashcardBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel

class EditFlashcardDialogFragment : DialogFragment() {

    private lateinit var binding: DialogEditFlashcardBinding
    private lateinit var alertDialog: AlertDialog

    private val viewModel: StudyViewModel by activityViewModels()


    companion object {
        private const val ARG_FLASHCARD_ID = "flashcard_id"
        fun newInstance(flashcardId: String): EditFlashcardDialogFragment {
            val fragment = EditFlashcardDialogFragment()
            fragment.arguments = Bundle().apply { putString(ARG_FLASHCARD_ID, flashcardId) }
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditFlashcardBinding.inflate(layoutInflater)
        val flashcardId = requireArguments().getString(ARG_FLASHCARD_ID)

        if (flashcardId == null) {
            Toast.makeText(requireContext(), "Error: Flashcard ID missing.", Toast.LENGTH_SHORT).show()
            dismiss()
            return super.onCreateDialog(savedInstanceState)!!
        }

        val flashcardToEdit = viewModel.getFlashcardById(flashcardId)

        if (flashcardToEdit == null) {
            Toast.makeText(requireContext(), "Flashcard not found.", Toast.LENGTH_SHORT).show()
            dismiss()
            return super.onCreateDialog(savedInstanceState)!!
        }

        binding.etQuestion.setText(flashcardToEdit.question)
        binding.etAnswer.setText(flashcardToEdit.answer)

        val questionLimit = 160
        val answerLimit = 250

        binding.questionCounter.text = getString(R.string.char_counter, flashcardToEdit.question.length, questionLimit)
        binding.answerCounter.text = getString(R.string.char_counter, flashcardToEdit.answer.length, answerLimit)

        binding.etQuestion.addTextChangedListener {
            binding.questionCounter.text = getString(R.string.char_counter, it?.length ?: 0, questionLimit)
        }
        binding.etAnswer.addTextChangedListener {
            binding.answerCounter.text = getString(R.string.char_counter, it?.length ?: 0, answerLimit)
        }

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Flashcard")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val updatedFlashcard = flashcardToEdit.copy(
                    question = binding.etQuestion.text.toString().trim(),
                    answer = binding.etAnswer.text.toString().trim()
                )
                viewModel.updateFlashcard(updatedFlashcard)
            }
            .setNegativeButton("Cancel", null)
            .create()

        return alertDialog
    }

    override fun onStart() {
        super.onStart()
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        positiveButton?.background = null
        negativeButton?.background = null
    }
}