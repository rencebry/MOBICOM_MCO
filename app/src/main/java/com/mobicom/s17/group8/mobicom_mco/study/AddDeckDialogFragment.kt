package com.mobicom.s17.group8.mobicom_mco.study

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddDeckBinding
import java.text.SimpleDateFormat
import java.util.*

class AddDeckDialogFragment(
    private val courseId: String,
    private val onDeckAdded: (Deck) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogAddDeckBinding
    private lateinit var alertDialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddDeckBinding.inflate(layoutInflater)

        val deckTitleLimit = 55
        binding.deckTitleCounter.text = getString(R.string.char_counter, 0, deckTitleLimit)

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
            val newDeck = Deck(
                deckId = UUID.randomUUID().toString(),
                courseId = courseId,
                deckTitle = binding.etDeckTitle.text.toString().trim(),
                dateCreated = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                cardCount = 0
            )
            onDeckAdded(newDeck)
            binding.etDeckTitle.setText("")
            binding.btnSave.isEnabled = false
        }

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Deck")
            .setView(binding.root)
            .create()

        return alertDialog
    }

    override fun onStart() {
        super.onStart()
        listOf(binding.btnClear, binding.btnCancel, binding.btnSave).forEach {
            it.background = null
        }
    }
}