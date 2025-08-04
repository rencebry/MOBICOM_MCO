package com.mobicom.s17.group8.mobicom_mco.study.deck

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddDeckBinding // Still reusing this layout
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel

class RenameDeckDialogFragment : DialogFragment() {

    private lateinit var binding: DialogAddDeckBinding

    private val viewModel: StudyViewModel by activityViewModels()


    // --- NEW: Companion object for safe argument passing ---
    companion object {
        private const val ARG_DECK_ID = "deck_id_to_rename"
        fun newInstance(deckId: String): RenameDeckDialogFragment {
            val fragment = RenameDeckDialogFragment()
            fragment.arguments = Bundle().apply { putString(ARG_DECK_ID, deckId) }
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddDeckBinding.inflate(layoutInflater)
        val deckId = requireArguments().getString(ARG_DECK_ID)

        // --- FETCH THE DECK OBJECT FROM THE VIEWMODEL ---
        val deckToRename = deckId?.let { viewModel.getDeckById(it) }

        if (deckToRename == null) {
            // This is a safety check. If the deck was deleted while the menu was open,
            // we just show a message and dismiss.
            Toast.makeText(requireContext(), "Deck not found. It may have been deleted.", Toast.LENGTH_SHORT).show()
            // We need to dismiss in a separate handler because onCreateDialog is too early.
            parentFragmentManager.beginTransaction().remove(this).commit()
            return super.onCreateDialog(savedInstanceState)!!
        }

        // --- POPULATE THE UI ---
        val deckTitleLimit = 55
        binding.etDeckTitle.setText(deckToRename.deckTitle)
        binding.deckTitleCounter.text = getString(R.string.char_counter, deckToRename.deckTitle.length, deckTitleLimit)

        // Logic for enabling/disabling the save button
        updateSaveButtonState()
        binding.etDeckTitle.addTextChangedListener {
            updateSaveButtonState()
            val length = it?.length ?: 0
            binding.deckTitleCounter.text = getString(R.string.char_counter, length, deckTitleLimit)
        }

        binding.btnClear.setOnClickListener { binding.etDeckTitle.setText("") }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val newTitle = binding.etDeckTitle.text.toString().trim()
            viewModel.renameDeck(deckToRename, newTitle)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Rename Deck")
            .setView(binding.root)
            .create()
    }

    private fun updateSaveButtonState() {
        val title = binding.etDeckTitle.text.toString().trim()
        val isEnabled = title.isNotEmpty()
        binding.btnSave.isEnabled = isEnabled
        binding.btnSave.setTextColor(
            if (isEnabled) "#5A8392".toColorInt()
            else requireContext().getColor(android.R.color.darker_gray)
        )
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        listOf(binding.btnClear, binding.btnCancel, binding.btnSave).forEach {
            it.background = null
        }
    }
}