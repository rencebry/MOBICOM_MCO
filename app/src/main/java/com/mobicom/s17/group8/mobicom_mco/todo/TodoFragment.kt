package com.mobicom.s17.group8.mobicom_mco.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentTodoListBinding

class TodoFragment : Fragment() {
    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!

    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up UI components
        setupSpinners()
        setupRecyclerView()

        binding.addTaskFab.setOnClickListener {
            // Handle add task button click
            // This could open a dialog or a new fragment to add a new task
            // For now, we can just show a message or log it
        }
    }

    private fun setupSpinners() {
        // Data for the Spinners
        val taskLists = listOf("My Task", "Work", "Personal", "MOBICOM")
        val taskTypes = arrayOf("Ongoing", "All", "Starred", "Missed", "Completed")
        val viewOptions = arrayOf("List", "Calendar")

        // Adapter for Task List Spinner
        val taskListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            taskLists
        )
        taskListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTaskList.adapter = taskListAdapter

        // Adapter for Task Type Spinner
        val taskTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            taskTypes
        )
        taskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTaskType.adapter = taskTypeAdapter

        // Adapter for View Options Spinner
        val viewOptionsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewOptions
        )
        viewOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerViewOption.adapter = viewOptionsAdapter
    }

    // Set up the RecyclerView with the TodoAdapter
    private fun setupRecyclerView() {
        // Initialize the task items with sample data
        val taskItems = getTodoData()

        todoAdapter = TodoAdapter(taskItems)

        binding.todoListRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
    }

    // Sample data for the Todo list
    private fun getTodoData(): List<Task> {
        return listOf(
            Task("Task Name 1", "Description for task 1", false, false, "06/28/25", "11:59PM", "My Task"),
            Task("Task Name 2", "Description for task 2", true, false, "Date", "Time", "Label 1"),
            Task("Task Name 3", "Description for task 3", false, true, "Date", "Time", "Label 2"),
            Task("Task Name 4", "Description for task 4", true, true, "Date", "Time", "Label 1"),
            Task("Task Name 5", "Description for task 5", false, false, "Date", "Time", "Label 1"),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // fragment lifecycle
    }
}