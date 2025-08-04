package com.mobicom.s17.group8.mobicom_mco.study.deck

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddDeckBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel

class AddDeckDialogFragment : DialogFragment() {

    private var _binding: DialogAddDeckBinding? = null
    private val binding get() = _binding!!

    // activityViewModels is a great way to get the shared ViewModel
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

    // Use onCreateView to inflate the view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddDeckBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Use onViewCreated for all logic and view setup
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = requireArguments().getString(ARG_COURSE_ID)
        if (courseId == null) {
            // If there's no courseId, we can't save, so just dismiss.
            dismiss()
            return
        }

        setupListeners(courseId)
    }

    private fun setupListeners(courseId: String) {
        binding.etDeckTitle.addTextChangedListener { text ->
            // Enable/disable save button based on whether the input is empty
            binding.btnSave.isEnabled = !text.isNullOrBlank()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val deckTitle = binding.etDeckTitle.text.toString().trim()
            viewModel.addDeck(courseId, deckTitle)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // This correctly sizes and styles the dialog window
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