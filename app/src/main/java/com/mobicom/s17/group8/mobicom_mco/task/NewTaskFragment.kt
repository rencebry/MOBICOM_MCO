package com.mobicom.s17.group8.mobicom_mco.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentNewTaskBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class NewTaskFragment : Fragment() {
    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasksViewModel by navGraphViewModels(R.id.nav_graph)
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

        setupTaskListSpinner()
        setupDueDateControls()
        setupSaveButton()

        binding.returnButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.submitBtn.setOnClickListener {
            saveNewTask()
        }
    }

    private fun setupSaveButton() {
        // Initially disable the save button
        binding.submitBtn.isEnabled = false

        binding.taskNameEtv.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.submitBtn.isEnabled = !s.isNullOrBlank()
            }
        })
    }

    private fun setupTaskListSpinner() {
        taskListsForSpinner = viewModel.allTaskLists.value
        val taskListTitles = taskListsForSpinner.map { it.title }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, taskListTitles)
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

        // --- STABLE TIMESTAMP LOGIC ---
        var dueTimestamp: String? = null
        if (binding.dueDateSwitch.isChecked) {
            val dateText = binding.editDueDate.text.toString()
            val timeText = binding.editDueTime.text.toString()

            if (dateText.isNotEmpty() && timeText.isNotEmpty()) {
                // Both date and time are set
                val localDateTime = LocalDateTime.of(
                    selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH) + 1,
                    selectedCalendar.get(Calendar.DAY_OF_MONTH), selectedCalendar.get(Calendar.HOUR_OF_DAY),
                    selectedCalendar.get(Calendar.MINUTE)
                )
                dueTimestamp = localDateTime.toInstant(ZoneOffset.UTC).toString()
            } else if (dateText.isNotEmpty()) {
                // Only the date is set
                val localDate = LocalDate.of(
                    selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH) + 1,
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
                )
                dueTimestamp = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
        }

        viewModel.addNewTask(
            title = title,
            notes = notes,
            due = dueTimestamp,
            taskListId = selectedList.id
        )
        findNavController().navigateUp()
    }

    private fun setupDueDateControls() {
        binding.editDueTime.isEnabled = false
        binding.dueDateSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.editDueDate.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.editDueTime.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                binding.editDueDate.text.clear()
                binding.editDueTime.text.clear()
                binding.editDueTime.isEnabled = false
            }
        }
        binding.editDueDate.setOnClickListener { showDatePicker() }
        binding.editDueTime.setOnClickListener { showTimePicker() }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedCalendar.set(year, month, dayOfMonth)
                // When a new date is picked, reset the time portion
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
                selectedCalendar.set(Calendar.MINUTE, 0)
                selectedCalendar.set(Calendar.SECOND, 0)
                binding.editDueTime.text.clear() // Clear the time field visually

                val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                binding.editDueDate.setText(sdf.format(selectedCalendar.time))
                binding.editDueTime.isEnabled = true
            },
            selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH),
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
            selectedCalendar.get(Calendar.HOUR_OF_DAY), selectedCalendar.get(Calendar.MINUTE),
            false
        )
        timePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}