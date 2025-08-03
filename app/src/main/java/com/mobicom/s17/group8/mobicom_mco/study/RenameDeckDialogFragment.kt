package com.mobicom.s17.group8.mobicom_mco.study

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddDeckBinding

class RenameDeckDialogFragment(
    private val deck: Deck,
    private val onRenameDeck: (Deck) -> Unit
) : DialogFragment() {

    private lateinit var alertDialog: AlertDialog  // Store reference to customize later
    private lateinit var binding: DialogAddDeckBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddDeckBinding.inflate(layoutInflater)

        val deckTitleLimit = 55
        binding.etDeckTitle.setText(deck.deckTitle)
            binding.etDeckTitle.setText(deck.deckTitle)
                    binding.deckTitleCounter.text = getString(R.string.char_counter, deck.deckTitle.length, deckTitleLimit)

        val updateSaveButtonState = {
            val title = binding.etDeckTitle.text.toString().trim()
            binding.btnSave.isEnabled = title.isNotEmpty()
            binding.btnSave.setTextColor(
                if (binding.btnSave.isEnabled) "#5A8392".toColorInt()
                else requireContext().getColor(android.R.color.darker_gray)
            )
        }

        binding.etDeckTitle.addTextChangedListener {
            val length = it?.length ?: 0
            binding.deckTitleCounter.text = getString(R.string.char_counter, length, deckTitleLimit)
            updateSaveButtonState()
        }

        binding.btnClear.setOnClickListener {
            binding.etDeckTitle.setText("")
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val updatedDeck = deck.copy(deckTitle = binding.etDeckTitle.text.toString().trim())
            onRenameDeck(updatedDeck)
            dismiss()
        }

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Rename Deck")
            .setView(binding.root)
            .create()

        return alertDialog
    }

    override fun onStart() {
        super.onStart()
        listOf(binding.btnSave, binding.btnCancel, binding.btnClear).forEach {
            it.background = null
        }
    }
}