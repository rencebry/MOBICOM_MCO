package com.mobicom.s17.group8.mobicom_mco.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
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
            Task(
                "1",
                "Task Name 1",
                "timestamp",
                "Description for task 1",
                false,
                "needsAction",
                "06/28/25 11:59PM",
                "06/28/25",
                "11:59PM",
                "My Task",
                "timestamp"
            ),
            Task(
                "2",
                "Task Name 2",
                "timestamp2",
                "Description for task 2",
                true,
                "completed",
                "06/29/25 10:00AM",
                "06/29/25",
                "10:00AM",
                "Work",
                "timestamp2"
            ),
            Task(
                "3",
                "Task Name 3",
                "timestamp3",
                "Description for task 3",
                false,
                "needsAction",
                "06/30/25 09:30AM",
                "06/30/25",
                "09:30AM",
                "Personal",
                "timestamp3"
            ),
            Task(
                "4",
                "Task Name 4",
                "timestamp4",
                "Description for task 4",
                true,
                "completed",
                "07/01/25 02:00PM",
                "07/01/25",
                "02:00PM",
                "MOBICOM",
                "timestamp4"
            ),
            Task(
                "5",
                "Task Name 5",
                "timestamp5",
                "Description for task 5",
                false,
                "needsAction",
                "07/02/25 04:15PM",
                "07/02/25",
                "04:15PM",
                "My Task",
                "timestamp5"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // fragment lifecycle
    }
}