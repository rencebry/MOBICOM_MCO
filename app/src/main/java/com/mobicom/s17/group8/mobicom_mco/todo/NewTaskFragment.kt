package com.mobicom.s17.group8.mobicom_mco.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentNewTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class NewTaskFragment : Fragment() {
    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!

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

        binding.submitBtn.setOnClickListener {
            // TO-DO: Handle the submission of the new task
            findNavController().navigateUp()
        }

        // Show/hide due date/time fields based on switch
        binding.dueDateSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.editDueDate.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.editDueTime.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Date picker for editDueDate
        binding.editDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val date = Calendar.getInstance()
                    date.set(year, month, dayOfMonth)
                    val sdf = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
                    binding.editDueDate.setText(sdf.format(date.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Time picker for editDueTime
        binding.editDueTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    cal.set(Calendar.MINUTE, minute)
                    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    binding.editDueTime.setText(sdf.format(cal.time))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePicker.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}