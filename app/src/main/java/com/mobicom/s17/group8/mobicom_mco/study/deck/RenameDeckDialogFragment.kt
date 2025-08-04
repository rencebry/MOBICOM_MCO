package com.mobicom.s17.group8.mobicom_mco.study.deck

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddDeckBinding // Use the new binding
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel
import kotlinx.coroutines.launch

class RenameDeckDialogFragment : DialogFragment() {

    private var _binding: DialogAddDeckBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudyViewModel by activityViewModels()

    // Companion object for safe argument passing
    companion object {
        private const val ARG_DECK_ID = "deck_id_to_rename"
        fun newInstance(deckId: String): RenameDeckDialogFragment {
            val fragment = RenameDeckDialogFragment()
            fragment.arguments = Bundle().apply { putString(ARG_DECK_ID, deckId) }
            return fragment
        }
    }

    // Use onCreateView to inflate the view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddDeckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deckId = requireArguments().getString(ARG_DECK_ID)
        if (deckId == null) {
            dismiss()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val deckToRename = viewModel.getDeckById(deckId)

            if (deckToRename == null) {
                Toast.makeText(requireContext(), "Deck not found.", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                // Populate the UI and set up listeners now that we have the data
                binding.etDeckTitle.setText(deckToRename.deckTitle)
                binding.etDeckTitle.setSelection(deckToRename.deckTitle.length)
                setupListeners(deckToRename)
            }
        }
    }

    private fun setupListeners(deckToRename: com.mobicom.s17.group8.mobicom_mco.database.study.Deck) {
        // Enable/disable the save button based on input
        binding.etDeckTitle.addTextChangedListener { text ->
            binding.btnSave.isEnabled = !text.isNullOrBlank()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val newTitle = binding.etDeckTitle.text.toString().trim()
            viewModel.renameDeck(deckToRename, newTitle)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}