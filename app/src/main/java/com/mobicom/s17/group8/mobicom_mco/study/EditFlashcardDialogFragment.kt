package com.mobicom.s17.group8.mobicom_mco.study

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogEditFlashcardBinding

class EditFlashcardDialogFragment(
    private val flashcard: Flashcard,
    private val onFlashcardEdited: (Flashcard) -> Unit
) : DialogFragment() {

    private lateinit var alertDialog: AlertDialog  // Store reference to customize later

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogEditFlashcardBinding.inflate(layoutInflater)

        binding.etQuestion.setText(flashcard.question)
        binding.etAnswer.setText(flashcard.answer)

        val questionLimit = 160
        val answerLimit = 250

        binding.questionCounter.text = getString(R.string.char_counter, flashcard.question.length, questionLimit)
        binding.answerCounter.text = getString(R.string.char_counter, flashcard.answer.length, answerLimit)

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
                val updated = flashcard.copy(
                    question = binding.etQuestion.text.toString(),
                    answer = binding.etAnswer.text.toString()
                )
                onFlashcardEdited(updated)
            }
            .setNegativeButton("Cancel", null)
            .create()

        return alertDialog
    }

    override fun onStart() {
        super.onStart()

        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        // Remove Material shape/background (fully transparent)
        positiveButton?.background = null
        negativeButton?.background = null
    }
}