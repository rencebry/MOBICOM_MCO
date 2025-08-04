package com.mobicom.s17.group8.mobicom_mco.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.navigation.fragment.findNavController
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentStudyBinding
import com.mobicom.s17.group8.mobicom_mco.study.courses.AddCourseDialogFragment
import com.mobicom.s17.group8.mobicom_mco.study.courses.CourseAdapter

class StudyFragment : Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth

    private val viewModel: StudyViewModel by viewModels {
        val userId = auth.currentUser?.uid ?: ""
        val database = AppDatabase.getDatabase(requireContext())
        val repository = StudyRepository(
            courseDao = database.courseDao(),
            deckDao = database.deckDao(),
            flashcardDao = database.flashcardDao(),
            userId = userId
        )
        StudyViewModelFactory(repository, userId)
    }
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.btnEdit.setOnClickListener {
            viewModel.toggleEditMode()
        }

        binding.fabAddCourse.setOnClickListener {
            AddCourseDialogFragment().show(parentFragmentManager, "AddCourseDialog")
        }

    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter(
            onCourseClicked = { course ->
                val action = StudyFragmentDirections.actionStudyFragmentToDeckListFragment(course.courseId, course.courseTitle)
                findNavController().navigate(action)
            },
            onDeleteClicked = { course ->
                // TODO: Show confirm delete dialog
                viewModel.deleteCourse(course.courseId)
            }
        )
        binding.rvCourses.adapter = courseAdapter
    }

    private fun observeViewModel() {
        viewModel.courses.observe(viewLifecycleOwner) { courseList ->
            if (courseList != null) {
                courseAdapter.submitList(courseList)
            }
        }

        viewModel.isEditMode.observe(viewLifecycleOwner) { isEditing ->
            courseAdapter.setEditMode(isEditing)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
