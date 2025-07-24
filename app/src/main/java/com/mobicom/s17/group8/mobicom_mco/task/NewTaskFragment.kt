package com.mobicom.s17.group8.mobicom_mco.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentNewTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import com.mobicom.s17.group8.mobicom_mco.database.Task

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

        // Fetch tasklists and set up the spinner
        setupTaskListSpinner()

        // Back button listener
        binding.returnButton.setOnClickListener {
            // Handle save button click
            findNavController().navigateUp()
        }

        // Submit button listener
        binding.submitBtn.setOnClickListener {
            // --- Begin edit ---
            var dueRfc3339: String? = null
            var dueDateStr: String? = null
            var dueTimeStr: String? = null
            if (binding.dueDateSwitch.isChecked) {
                dueDateStr = binding.editDueDate.text.toString()
                dueTimeStr = binding.editDueTime.text.toString()
                if (dueDateStr.isNotEmpty() && dueTimeStr.isNotEmpty()) {
                    try {
                        // Parse input date and time
                        val inputFormat = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault())
                        val date = inputFormat.parse("$dueDateStr $dueTimeStr")
                        // Format to RFC 3339 (yyyy-MM-dd'T'HH:mm:ss'Z')
                        val rfc3339Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        rfc3339Format.timeZone = TimeZone.getTimeZone("UTC")
                        dueRfc3339 = rfc3339Format.format(date!!)
                    } catch (e: Exception) {
                        dueRfc3339 = null
                    }
                }
            }
            // --- End edit ---

            val selectedTaskListId = binding.taskListSpinner.selectedItem?.toString() ?: ""
            val label = "My Task" // You can update this if you add a label spinner

            val newTask = Task(
                id = UUID.randomUUID().toString(),
                title = binding.taskNameEtv.text.toString(),
                status = "needsAction",
                due = dueRfc3339,
                notes = binding.taskDetailsEtv.text.toString(),
                updated = System.currentTimeMillis().toString(),
                tasklistId = selectedTaskListId,
                completed = null, // Not completed on creation
                parent = null, // No parent by default
                position = "0", // Default position
                isSynced = false, // Not synced on creation
                isDeleted = false, // Not deleted on creation
                isCompleted = false, // Not completed on creation
                dueDate = dueDateStr,
                dueTime = dueTimeStr,
                label = label
            )
            saveTask(newTask)
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

    private fun setupTaskListSpinner() {
        CoroutineScope(Dispatchers.IO).launch {
            val taskLists = AppDatabase.getDatabase(requireContext()).taskListDao().getAllTaskLists()
            val tasListTitles = taskLists.map { it.id to it.title }.toMap()

            withContext(Dispatchers.Main) {
                val taskListAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    tasListTitles.values.toList()
                )
                taskListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.taskListSpinner.adapter = taskListAdapter

                // Set the default selection to the first item
                if (tasListTitles.isNotEmpty()) {
                    binding.taskListSpinner.setSelection(0)
                }
            }
        }
    }

    private fun saveTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(requireContext()).taskDao().insertTask(task)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}