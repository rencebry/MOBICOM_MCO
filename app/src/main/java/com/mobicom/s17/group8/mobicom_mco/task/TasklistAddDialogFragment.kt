package com.mobicom.s17.group8.mobicom_mco.task

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddTasklistBinding
import java.util.UUID
import androidx.fragment.app.viewModels

class TasklistAddDialogFragment : DialogFragment() {
    private var _binding: DialogAddTasklistBinding? = null
    private val binding get() = _binding!!

    private val tasksViewModel: TasksViewModel by viewModels({ requireParentFragment() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DialogAddTasklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTaskListName.text.toString().trim()
            if (title.isNotEmpty()) {
                tasksViewModel.insertNewTaskList(title)
                dismiss()
            } else {
                binding.etTaskListName.error = "Title cannot be empty"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}