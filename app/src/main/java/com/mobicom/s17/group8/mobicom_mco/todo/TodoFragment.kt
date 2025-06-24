package com.mobicom.s17.group8.mobicom_mco.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentTodoListBinding

class TodoFragment : Fragment() {
    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!

    private lateinit var todoAdapter: TodoAdapter
    private val taskItems = getTodoData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // fragment lifecycle
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }


     private fun setupRecyclerView() {
         todoAdapter = TodoAdapter(taskItems) { task ->
            val bundle = Bundle().apply {
                putString("task name", task.name)
                putString("task details", task.details)
                putBoolean("task completed", task.isCompleted)
                putBoolean("task starred", task.isStarred)
                putString("task date", task.dueDate)
                putString("task time", task.dueTime)
                putString("task label", task.label)
            }
         }
        //val fragment = DetailsFragment() // fragment for task details
          //  fragment.arguments = bundle
//         parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, DetailsFragment::class.java, bundle)
//                .addToBackStack(null)
//                .commit()
         binding.todoListRv.apply {
             adapter = todoAdapter
         }
     }




    // Sample data for the Todo list
    private fun getTodoData(): List<Task> {
        return listOf(
            Task("Task Name 1", "Description for task 1", false, false, "Date", "Time", "Label"),
            Task("Task Name 2", "Description for task 2", true, false, "Date", "Time", "Label"),
            Task("Task Name 3", "Description for task 3", false, true, "Date", "Time", "Label"),
            Task("Task Name 4", "Description for task 4", true, true, "Date", "Time", "Label"),
            Task("Task Name 5", "Description for task 5", false, false, "Date", "Time", "Label"),
        )
    }
}