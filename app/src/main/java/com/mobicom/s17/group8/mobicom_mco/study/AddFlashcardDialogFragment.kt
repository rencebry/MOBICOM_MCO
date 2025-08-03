package com.mobicom.s17.group8.mobicom_mco.study

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddFlashcardBinding
import java.util.UUID
import androidx.core.graphics.toColorInt

class AddFlashcardDialogFragment(
    private val deckId: String,
    private val courseId: String,
    private val onFlashcardAdded: (Flashcard) -> Unit
) : DialogFragment() {

    private lateinit var alertDialog: AlertDialog
    private lateinit var binding: DialogAddFlashcardBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddFlashcardBinding.inflate(layoutInflater)

        val questionLimit = 160
        val answerLimit = 250

        // Initial counter
        binding.questionCounter.text = getString(R.string.char_counter, 0, questionLimit)
        binding.answerCounter.text = getString(R.string.char_counter, 0, answerLimit)

        // Enable Save only when both fields are non-empty
        val updateSaveButtonState = {
            val question = binding.etQuestion.text.toString().trim()
            val answer = binding.etAnswer.text.toString().trim()
            binding.btnSave.isEnabled = question.isNotEmpty() && answer.isNotEmpty()
            binding.btnSave.setTextColor(
                if (binding.btnSave.isEnabled) "#5A8392".toColorInt()
                else requireContext().getColor(R.color.gray)
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
            val flashcard = Flashcard(
                flashcardId = UUID.randomUUID().toString(),
                deckId = deckId,
                courseId = courseId,
                question = binding.etQuestion.text.toString(),
                answer = binding.etAnswer.text.toString()
            )
            onFlashcardAdded(flashcard)
            binding.etQuestion.setText("")
            binding.etAnswer.setText("")
            binding.btnSave.isEnabled = false
        }

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Flashcard")
            .setView(binding.root)
            .create()

        return alertDialog
    }

    override fun onStart() {
        super.onStart()
        // Remove material background from the buttons
        listOf(binding.btnClear, binding.btnCancel, binding.btnSave).forEach {
            it.background = null
        }
    }
}