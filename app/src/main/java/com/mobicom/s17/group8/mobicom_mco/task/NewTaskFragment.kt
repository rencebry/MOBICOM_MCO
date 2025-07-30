package com.mobicom.s17.group8.mobicom_mco.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentNewTaskBinding
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class NewTaskFragment : Fragment() {
    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!

    // Get a reference to the same TasksViewModel instance as the parent fragment uses
    private val viewModel: TasksViewModel by navGraphViewModels(R.id.nav_graph)

    // full TaskList objects for the spinner
    private var taskListsForSpinner: List<TaskList> = emptyList()
    private val selectedCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch tasklists and set up the spinner
        setupTaskListSpinner()
        setupDueDateControls()

        // Back button listener
        binding.returnButton.setOnClickListener {
            // Handle save button click
            findNavController().navigateUp()
        }

        // Submit button listener
        binding.submitBtn.setOnClickListener {
            saveNewTask()
        }
    }

    private fun setupTaskListSpinner() {
        // Get list of TaskLists directly from the ViewModel's state
        taskListsForSpinner = viewModel.allTaskLists.value
        val taskListTitles = taskListsForSpinner.map { it.title }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            taskListTitles
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.taskListSpinner.adapter = adapter

        val currentlySelectedListId = viewModel.selectedTaskListId.value
        val selectionIndex = taskListsForSpinner.indexOfFirst { it.id == currentlySelectedListId }
        if (selectionIndex != -1) {
            binding.taskListSpinner.setSelection(selectionIndex)
        }
    }

    private fun saveNewTask() {
        val title = binding.taskNameEtv.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Task name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val notes = binding.taskDetailsEtv.text.toString().trim()

        val selectedPosition = binding.taskListSpinner.selectedItemPosition
        if (selectedPosition < 0 || selectedPosition >= taskListsForSpinner.size) {
            Toast.makeText(requireContext(), "Please select a valid list", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedList = taskListsForSpinner[selectedPosition]

        // Handle due date and time
        var dueTimestamp: String? = null
        if (binding.dueDateSwitch.isChecked){
            // Combine date and time from the Calendar objects into a LocalDateTime
            val localDateTime = LocalDateTime.of(
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH) + 1, // Calendar.MONTH is zero-based
                selectedCalendar.get(Calendar.DAY_OF_MONTH),
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE)
            )
            // Convert to Instant (UTC) and then to an RFC 3339 string
            dueTimestamp = localDateTime.toInstant(ZoneOffset.UTC).toString() // This will give you the ISO 8601 format
        }

        // Call viewmodel to save the task
        viewModel.addNewTask(
            title = title,
            notes = notes,
            due = dueTimestamp,
            taskListId = selectedList.id
        )
        findNavController().navigateUp()
    }

    private fun setupDueDateControls() {
        binding.dueDateSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.editDueDate.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.editDueTime.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        binding.editDueDate.setOnClickListener { showDatePicker() }
        binding.editDueTime.setOnClickListener { showTimePicker() }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedCalendar.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                binding.editDueDate.setText(sdf.format(selectedCalendar.time))
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePicker() {
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                binding.editDueTime.setText(sdf.format(selectedCalendar.time))
            },
            selectedCalendar.get(Calendar.HOUR_OF_DAY),
            selectedCalendar.get(Calendar.MINUTE),
            false // Use 12-hour format with AM/PM
        )
        timePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}