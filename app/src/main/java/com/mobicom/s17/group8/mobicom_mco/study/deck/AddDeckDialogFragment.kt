package com.mobicom.s17.group8.mobicom_mco.study.deck

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider // Add this import
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddDeckBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyRepository
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModelFactory

class AddDeckDialogFragment : DialogFragment() {

    private var _binding: DialogAddDeckBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudyViewModel by lazy {
        val activity = requireActivity()
        val userId = Firebase.auth.currentUser?.uid ?: ""
        val database = AppDatabase.getDatabase(activity.applicationContext)
        val repository = StudyRepository(
            courseDao = database.courseDao(),
            deckDao = database.deckDao(),
            flashcardDao = database.flashcardDao(),
            userId = userId
        )
        val factory = StudyViewModelFactory(repository, userId)
        ViewModelProvider(activity, factory).get(StudyViewModel::class.java)
    }

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
            dismiss()
            return
        }

        setupListeners(courseId)
    }

    private fun setupListeners(courseId: String) {
        binding.etDeckTitle.addTextChangedListener { text ->
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