package com.mobicom.s17.group8.mobicom_mco.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentTodoListBinding
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskRepository
import com.mobicom.s17.group8.mobicom_mco.auth.UserAuthViewModel
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddTasklistBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.fragment.app.activityViewModels


class TaskFragment : Fragment(R.layout.fragment_todo_list) {
    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!

    private val userAuthViewModel: UserAuthViewModel by activityViewModels()

    private lateinit var taskListAdapter: TaskListAdapter
    private lateinit var taskAdapter: TaskAdapter

    private val viewModel: TasksViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())

        val repository = TaskRepository(database.taskDao(), database.taskListDao())

        val userId = userAuthViewModel.currentUserId.value!!

        TaskViewModelFactory(repository, userId)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // val loggedInUserId = viewModel.getLoggedInUserId()

        // Set up UI components
        setupSpinners()
        setupAdapters()
        setupRecyclerViews()
        observeViewModel()

//        viewModel.allTaskLists.value.ifEmpty {
//            viewModel.addDefaultTaskList()
//        }

        binding.addTaskFab.setOnClickListener {
            // Handle add task button click
            //findNavController().navigate(R.id.action_nav_todo_to_new_task)
        }
    }

    private fun setupAdapters() {

        taskListAdapter = TaskListAdapter (
            onTaskListClicked = { taskList -> viewModel.selectTaskList(taskList.id)},
            onAddNewListClicked = { TasklistAddDialogFragment().show(childFragmentManager, "AddTaskListDialog") }
        )

        //binding.taskListRv.adapter = taskListAdapter
        taskAdapter = TaskAdapter()

    }


    private fun setupRecyclerViews() {
        // Horizontal RecyclerView for TaskLists
        binding.taskListRv.adapter = taskListAdapter
        // layout manager already set in XML

        // Vertical RecyclerView for Tasks
        binding.todoListRv.adapter = taskAdapter
        binding.todoListRv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Coroutine to collect the list of task lists
                launch {
                    viewModel.allTaskLists.collectLatest { taskLists ->
                        taskListAdapter.submitList(taskLists)
                        // Automatically select the first list if non is selected
                        if (taskLists.isNotEmpty() && viewModel.tasksForSelectedList.value.isEmpty()) {
                            if (viewModel.tasksForSelectedList.value == null) {
                                viewModel.selectTaskList(taskLists.first().id)
                            }
                        }
                    }
                }
                // Coroutine to collect tasks for the selected task list
                launch {
                    viewModel.tasksForSelectedList.collectLatest { tasks ->
                        val selectedId = viewModel.selectedTaskListId.value

                        val selectedList = viewModel.allTaskLists.value.find { it.id == selectedId }
                        taskAdapter.currentTaskListName = selectedList?.title ?: "Tasks"
                        taskAdapter.submitList(tasks)
                    }
                }
                // Coroutine to observe the selected task list ID and update the adapter's visual state
                launch {
                    viewModel.selectedTaskListId.collectLatest { newSelectedId ->
                        taskListAdapter.setSelectedId(newSelectedId)
                        val position = viewModel.allTaskLists.value.indexOfFirst { it.id == newSelectedId }
                        if (position != -1) {
                            (binding.taskListRv.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 0)
                        }
                    }
                }
            }
        }
    }

    private fun setupSpinners() {
        // Data for the Spinners
        val taskTypes = arrayOf("Ongoing", "All", "Missed", "Completed")
        val viewOptions = arrayOf("List", "Calendar")

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

    // Sample data for the Todo list
//    private fun getTodoData(): List<Task> {
//        return listOf(
//            Task(
//                id = "1",
//                tasklistId = "list1",
//                title = "Task Name 1",
//                status = "needsAction",
//                due = "2025-06-28T23:59:00Z",
//                notes = "Description for task 1",
//                updated = "2025-06-20T10:00:00Z",
//                completed = null,
//                parent = null,
//                position = "1",
//                isSynced = false,
//                isDeleted = false,
//                isCompleted = false,
//                dueDate = "06/28/25",
//                dueTime = "11:59PM",
//            ),
//            Task(
//                id = "2",
//                tasklistId = "list2",
//                title = "Task Name 2",
//                status = "completed",
//                due = "2025-06-29T10:00:00Z",
//                notes = "Description for task 2",
//                updated = "2025-06-21T11:00:00Z",
//                completed = "2025-06-29T10:00:00Z",
//                parent = null,
//                position = "2",
//                isSynced = true,
//                isDeleted = false,
//                isCompleted = true,
//                dueDate = "06/29/25",
//                dueTime = "10:00AM",
//            ),
//            Task(
//                id = "3",
//                tasklistId = "list3",
//                title = "Task Name 3",
//                status = "needsAction",
//                due = "2025-06-30T09:30:00Z",
//                notes = "Description for task 3",
//                updated = "2025-06-22T12:00:00Z",
//                completed = null,
//                parent = null,
//                position = "3",
//                isSynced = false,
//                isDeleted = false,
//                isCompleted = false,
//                dueDate = "06/30/25",
//                dueTime = "09:30AM",
//            ),
//            Task(
//                id = "4",
//                tasklistId = "list4",
//                title = "Task Name 4",
//                status = "completed",
//                due = "2025-07-01T14:00:00Z",
//                notes = "Description for task 4",
//                updated = "2025-06-23T13:00:00Z",
//                completed = "2025-07-01T14:00:00Z",
//                parent = null,
//                position = "4",
//                isSynced = true,
//                isDeleted = false,
//                isCompleted = true,
//                dueDate = "07/01/25",
//                dueTime = "02:00PM",
//            ),
//            Task(
//                id = "5",
//                tasklistId = "list1",
//                title = "Task Name 5",
//                status = "needsAction",
//                due = "2025-07-02T16:15:00Z",
//                notes = "Description for task 5",
//                updated = "2025-06-24T14:00:00Z",
//                completed = null,
//                parent = null,
//                position = "5",
//                isSynced = false,
//                isDeleted = false,
//                isCompleted = false,
//                dueDate = "07/02/25",
//                dueTime = "04:15PM",
//            )
//        )
//    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.taskListRv.adapter = null
        binding.todoListRv.adapter = null
        _binding = null
    }
}