package com.mobicom.s17.group8.mobicom_mco.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.Task
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
            findNavController().navigate(R.id.action_nav_todo_to_new_task)
        }
    }

    private fun setupSpinners() {
        // Data for the Spinners
        // Task Lists need to be updated with actual task lists from the database or API
        val taskLists = listOf("My Task", "Work", "Personal", "MOBICOM")
        val taskTypes = arrayOf("Ongoing", "All", "Starred", "Missed", "Completed")
        val viewOptions = arrayOf("List", "Calendar")

        // Adapter for Task List Spinner
        val taskListAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_task_list,
            taskLists
        )
        taskListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTaskList.adapter = taskListAdapter

        // Adapter for Task Type Spinner
        val taskTypeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_default,
            taskTypes
        )
        taskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTaskType.adapter = taskTypeAdapter

        // Adapter for View Options Spinner
        val viewOptionsAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_default,
            viewOptions
        )
        viewOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerViewOption.adapter = viewOptionsAdapter
    }

    // Set up the RecyclerView with the TodoAdapter
    private fun setupRecyclerView() {
        // TODO: Initialize the task items with data from Room database
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
            Task(
                id = "1",
                title = "Task Name 1",
                status = "needsAction",
                due = "2025-06-28T23:59:00Z",
                notes = "Description for task 1",
                updated = "2025-06-20T10:00:00Z",
                completed = null,
                parent = null,
                position = "1",
                tasklistId = "list1",
                isSynced = false,
                isDeleted = false,
                isCompleted = false,
                dueDate = "06/28/25",
                dueTime = "11:59PM",
                label = "My Task"
            ),
            Task(
                id = "2",
                title = "Task Name 2",
                status = "completed",
                due = "2025-06-29T10:00:00Z",
                notes = "Description for task 2",
                updated = "2025-06-21T11:00:00Z",
                completed = "2025-06-29T10:00:00Z",
                parent = null,
                position = "2",
                tasklistId = "list2",
                isSynced = true,
                isDeleted = false,
                isCompleted = true,
                dueDate = "06/29/25",
                dueTime = "10:00AM",
                label = "Work"
            ),
            Task(
                id = "3",
                title = "Task Name 3",
                status = "needsAction",
                due = "2025-06-30T09:30:00Z",
                notes = "Description for task 3",
                updated = "2025-06-22T12:00:00Z",
                completed = null,
                parent = null,
                position = "3",
                tasklistId = "list3",
                isSynced = false,
                isDeleted = false,
                isCompleted = false,
                dueDate = "06/30/25",
                dueTime = "09:30AM",
                label = "Personal"
            ),
            Task(
                id = "4",
                title = "Task Name 4",
                status = "completed",
                due = "2025-07-01T14:00:00Z",
                notes = "Description for task 4",
                updated = "2025-06-23T13:00:00Z",
                completed = "2025-07-01T14:00:00Z",
                parent = null,
                position = "4",
                tasklistId = "list4",
                isSynced = true,
                isDeleted = false,
                isCompleted = true,
                dueDate = "07/01/25",
                dueTime = "02:00PM",
                label = "MOBICOM"
            ),
            Task(
                id = "5",
                title = "Task Name 5",
                status = "needsAction",
                due = "2025-07-02T16:15:00Z",
                notes = "Description for task 5",
                updated = "2025-06-24T14:00:00Z",
                completed = null,
                parent = null,
                position = "5",
                tasklistId = "list1",
                isSynced = false,
                isDeleted = false,
                isCompleted = false,
                dueDate = "07/02/25",
                dueTime = "04:15PM",
                label = "My Task"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // fragment lifecycle
    }
}