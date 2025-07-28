package com.mobicom.s17.group8.mobicom_mco.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentStudyBinding


class StudyFragment : Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyViewModel by lazy {
        ViewModelProvider(this).get(StudyViewModel::class.java)
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
                val action = StudyFragmentDirections.actionStudyFragmentToDeckListFragment(course.id, course.name)
                findNavController().navigate(action)
            },
            onDeleteClicked = { course ->
                // TODO: Show confirm delete dialog
                viewModel.deleteCourse(course.id)
            }
        )
        binding.rvCourses.adapter = courseAdapter
    }

    private fun observeViewModel() {
        viewModel.courses.observe(viewLifecycleOwner) { courseList ->
            courseAdapter.submitList(courseList)
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