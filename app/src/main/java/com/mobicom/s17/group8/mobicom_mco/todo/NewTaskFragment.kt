package com.mobicom.s17.group8.mobicom_mco.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentNewTaskBinding

class NewTaskFragment : Fragment() {
    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!

    // State variable to track if the task is starred
    private var isStarred: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button listener
        binding.returnButton.setOnClickListener {
            // Handle save button click
            findNavController().navigateUp()
        }

        binding.starredBtn.setOnClickListener {
            isStarred = !isStarred
            if (isStarred) {
                // Set the filled star icon (assuming you have a 'starred.xml' or similar)
                binding.starredBtn.setImageResource(R.drawable.star)
            } else {
                // Set the bordered star icon from your layout
                binding.starredBtn.setImageResource(R.drawable.star_border)
            }
        }

        binding.submitBtn.setOnClickListener {
            // TO-DO: Handle the submission of the new task
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}