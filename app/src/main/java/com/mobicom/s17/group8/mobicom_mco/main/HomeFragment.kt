package com.mobicom.s17.group8.mobicom_mco.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.auth.LandingActivity
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentHomeBinding
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemTaskBinding
import com.mobicom.s17.group8.mobicom_mco.todo.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// --- local adapter since adding tasks are not yet implemented ---
class HomeTaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<HomeTaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.apply {
            taskNameTv.text = task.name
            taskInfoTv.text = "${task.label} | ${task.dueDate}"
            taskCheckbox.isChecked = task.isCompleted
            starredIv.visibility = if (task.isStarred) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount() = tasks.size
}

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userName = "Kyoka" // placehlder
        binding.tvGreeting.text = getString(R.string.hello_user, userName) //placehlder
        setDate()

        binding.ivSettings.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnStartStudying.setOnClickListener {
            findNavController().navigate(R.id.nav_study)
        }

        binding.profileCard.apply {
            tvNameValue.text = "Kyoka"
            tvSchoolValue.text = "DLSU-M"
            tvCourseValue.text = "BS CS-ST"
            tvBirthdayValue.text = "06-04-2003"
            tvYearLevelValue.text = "III"
        }

        val tasksForToday = getPlaceholderTasks()
        val homeTaskAdapter = HomeTaskAdapter(tasksForToday)
        binding.rvHomeTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = homeTaskAdapter
        }
    }

    private fun setDate() {
        val monthDayFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
        val date = Date()
        val day = SimpleDateFormat("d", Locale.getDefault()).format(date).toInt()

        val daySuffix = getDayOfMonthSuffix(day)

        val formattedDate = "${monthDayFormat.format(date)}${daySuffix}"

        binding.tvDate.text = formattedDate
    }

    private fun getDayOfMonthSuffix(n: Int): String {
        if (n in 11..13) {
            return "th"
        }
        return when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Logout") { dialog, which ->
                logoutUser()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getPlaceholderTasks(): List<Task> {
        return listOf(
            Task("Finals Exam", null, false, true, "June 28", null, "STCLOUD"),
            Task("MCO Presentation", null, false, false, "June 30", null, "MOBICOM"),
            Task("Review for Quiz 2", null, true, false, "July 02", null, "CSARCH2")
        )
    }

    private fun logoutUser() {
        // Intent to go back to the LandingActivity
        val intent = Intent(requireActivity(), LandingActivity::class.java).apply {
            // clear the entire task stack
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // fragment lifecycle
    }
}