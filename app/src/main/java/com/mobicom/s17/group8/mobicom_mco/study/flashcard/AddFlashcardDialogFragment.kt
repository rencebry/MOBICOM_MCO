package com.mobicom.s17.group8.mobicom_mco.study.flashcard

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddFlashcardBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyRepository
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModelFactory
import kotlinx.coroutines.launch

class AddFlashcardDialogFragment : DialogFragment() {

    private var _binding: DialogAddFlashcardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudyViewModel by viewModels {
        val activity = requireActivity()
        val userId = Firebase.auth.currentUser?.uid ?: ""
        val database = AppDatabase.getDatabase(activity.applicationContext)
        val repository = StudyRepository(
            courseDao = database.courseDao(),
            deckDao = database.deckDao(),
            flashcardDao = database.flashcardDao(),
            userId = userId
        )
        StudyViewModelFactory(repository, userId)
    }

    companion object {
        private const val ARG_DECK_ID = "deck_id"
        private const val ARG_COURSE_ID = "course_id"

        fun newInstance(deckId: String, courseId: String): AddFlashcardDialogFragment {
            return AddFlashcardDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DECK_ID, deckId)
                    putString(ARG_COURSE_ID, courseId)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddFlashcardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deckId = requireArguments().getString(ARG_DECK_ID)!!
        val courseId = requireArguments().getString(ARG_COURSE_ID)!!

        val updateSaveButtonState = {
            val question = binding.etQuestion.text.toString().trim()
            val answer = binding.etAnswer.text.toString().trim()
            binding.btnSave.isEnabled = question.isNotEmpty() && answer.isNotEmpty()
        }

        binding.etQuestion.addTextChangedListener { updateSaveButtonState() }
        binding.etAnswer.addTextChangedListener { updateSaveButtonState() }
        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val question = binding.etQuestion.text.toString().trim()
            val answer = binding.etAnswer.text.toString().trim()

            if (question.isNotEmpty() && answer.isNotEmpty()) {
                lifecycleScope.launch {
                    val job = viewModel.addFlashcardAsync(deckId, courseId, question, answer)
                    job.join()
                    Toast.makeText(requireContext(), "Flashcard saved!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill out both question and answer.", Toast.LENGTH_SHORT).show()
            }
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