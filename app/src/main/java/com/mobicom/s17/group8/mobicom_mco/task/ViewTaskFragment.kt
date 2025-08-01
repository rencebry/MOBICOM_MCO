package com.mobicom.s17.group8.mobicom_mco.task

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskList
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentViewTaskBinding
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedDate
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedTime
import java.time.Instant
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import java.util.Calendar
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ViewTaskFragment : Fragment() {
    private var _binding: FragmentViewTaskBinding? = null
    private val binding get() = _binding!!

    private val args: ViewTaskFragmentArgs by navArgs()
    private val viewModel: TasksViewModel by navGraphViewModels(R.id.nav_graph)

    private var taskListsForSpinner: List<TaskList> = emptyList()
    private val selectedCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadTaskDetails(args.taskId)

        setupUI()
        observeViewModel()
    }


    private fun setupUI() {
        binding.returnButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.moreBtn.setOnClickListener {
            showDeleteMenu(it)
        }
        binding.markCompletedBtn.setOnClickListener {
            viewModel.toggleTaskCompletion()
        }

        setupDueDateControls()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewedTask.collectLatest { task ->
                if (task == null) {
                    if (isResumed) findNavController().navigateUp()
                } else {
                    updateUiWithTask(task)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.taskMarkedCompleteEvent.collect {
                Snackbar.make(binding.root, "Task completed", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { viewModel.undoToggleCompletion() }
                    .show()
                findNavController().navigateUp()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun updateUiWithTask(task: Task) {
        binding.taskNameEtv.setText(task.title)
        binding.taskDetailsEtv.setText(task.notes)
        binding.markCompletedBtn.text = if (task.status == "completed") "Mark uncompleted" else "Mark completed"

        val hasDueDate = task.due != null
        binding.dueDateSwitch.isChecked = hasDueDate
        updateDueDateVisibility(hasDueDate)
        if (hasDueDate) {
            binding.editDueDate.setText(task.due.toFormattedDate())
            binding.editDueTime.setText(task.due.toFormattedTime())
            task.due?.let {
                val isTimeSet = it.contains("T", ignoreCase = true)
                val instant = if (isTimeSet) Instant.parse(it) else LocalDate.parse(it).atStartOfDay().toInstant(ZoneOffset.UTC)
                selectedCalendar.timeInMillis = instant.toEpochMilli()
            }
        }

        taskListsForSpinner = viewModel.allTaskLists.value
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, taskListsForSpinner.map { it.title })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.taskListSpinner.adapter = adapter
        val selectionIndex = taskListsForSpinner.indexOfFirst { it.id == task.tasklistId }
        if (selectionIndex != -1) {
            binding.taskListSpinner.setSelection(selectionIndex)
        }
    }

    private fun autoSaveTask() {
        val currentTask = viewModel.viewedTask.value ?: return // Get the fresh task from ViewModel

        // Derive state directly from UI, not flags
        val newTitle = binding.taskNameEtv.text.toString().trim()
        val newNotes = binding.taskDetailsEtv.text.toString().trim()

        val selectedPosition = binding.taskListSpinner.selectedItemPosition
        if (selectedPosition < 0 || selectedPosition >= taskListsForSpinner.size) return
        val selectedList = taskListsForSpinner[selectedPosition]
        val newTaskListId = selectedList.id

        var dueTimestamp: String? = null
        if (binding.dueDateSwitch.isChecked) {
            val dateText = binding.editDueDate.text.toString()
            val timeText = binding.editDueTime.text.toString()
            if (dateText.isNotEmpty() && timeText.isNotEmpty()) {
                // Both are set
                val localDateTime = LocalDateTime.of(
                    selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH) + 1,
                    selectedCalendar.get(Calendar.DAY_OF_MONTH), selectedCalendar.get(Calendar.HOUR_OF_DAY),
                    selectedCalendar.get(Calendar.MINUTE)
                )
                dueTimestamp = localDateTime.toInstant(ZoneOffset.UTC).toString()
            } else if (dateText.isNotEmpty()) {
                // Only date is set
                val localDate = LocalDate.of(
                    selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH) + 1,
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
                )
                dueTimestamp = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
        }

        // Only update if something actually changed to prevent loops
        if (currentTask.title != newTitle || currentTask.notes != newNotes || currentTask.due != dueTimestamp || currentTask.tasklistId != newTaskListId) {
            viewModel.updateTask(newTitle, newNotes, dueTimestamp, newTaskListId)
        }
    }

    private fun setupDueDateControls() {
        binding.editDueTime.isEnabled = !binding.editDueDate.text.isNullOrEmpty()
        binding.dueDateSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateDueDateVisibility(isChecked)
            if (!isChecked) {
                binding.editDueDate.text.clear()
                binding.editDueTime.text.clear()
                binding.editDueTime.isEnabled = false
            }
        }
        binding.editDueDate.setOnClickListener { showDatePicker() }
        binding.editDueTime.setOnClickListener { showTimePicker() }
    }

    private fun updateDueDateVisibility(isVisible: Boolean) {
        binding.editDueDate.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.editDueTime.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onStop() {
        super.onStop()
        // Save all changes when the user leaves the screen. This is a simple and robust model.
        saveTaskChanges()
    }

    private fun saveTaskChanges() {
        val currentTask = viewModel.viewedTask.value ?: return
        val newTitle = binding.taskNameEtv.text.toString().trim()
        val newNotes = binding.taskDetailsEtv.text.toString().trim()

        val selectedPosition = binding.taskListSpinner.selectedItemPosition
        if (selectedPosition < 0 || selectedPosition >= taskListsForSpinner.size) return
        val selectedList = taskListsForSpinner[selectedPosition]
        val newTaskListId = selectedList.id

        // --- STABLE TIMESTAMP LOGIC ---
        var dueTimestamp: String? = null
        if (binding.dueDateSwitch.isChecked) {
            val dateText = binding.editDueDate.text.toString()
            val timeText = binding.editDueTime.text.toString()
            if (dateText.isNotEmpty() && timeText.isNotEmpty()) {
                val localDateTime = LocalDateTime.of(
                    selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH) + 1,
                    selectedCalendar.get(Calendar.DAY_OF_MONTH), selectedCalendar.get(Calendar.HOUR_OF_DAY),
                    selectedCalendar.get(Calendar.MINUTE)
                )
                dueTimestamp = localDateTime.toInstant(ZoneOffset.UTC).toString()
            } else if (dateText.isNotEmpty()) {
                val localDate = LocalDate.of(
                    selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH) + 1,
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
                )
                dueTimestamp = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
        }

        viewModel.updateTask(newTitle, newNotes, dueTimestamp, newTaskListId)
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedCalendar.set(year, month, dayOfMonth)
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
                selectedCalendar.set(Calendar.MINUTE, 0)
                binding.editDueTime.text.clear()
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

    private fun showDeleteMenu(anchorView: View) {
        PopupMenu(requireContext(), anchorView).apply {
            menu.add("Delete").setOnMenuItemClickListener {
                showDeleteConfirmationDialog()
                true
            }
            show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete task?")
            .setMessage("Are you sure you want to permanently delete this task?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTask()
                findNavController().navigateUp()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}