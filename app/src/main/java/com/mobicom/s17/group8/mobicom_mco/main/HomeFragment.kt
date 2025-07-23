package com.mobicom.s17.group8.mobicom_mco.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.util.Log
import androidx.lifecycle.lifecycleScope
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// --- local adapter since adding tasks are not yet implemented ---
class HomeTaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<HomeTaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.apply {
            taskNameTv.text = task.name
            taskInfoTv.text = "${task.label} | ${task.dueDate}"
            taskCheckbox.isChecked = task.isCompleted
            //starredIv.visibility = if (task.isStarred) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount() = tasks.size
}

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth
    private val db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserProfile()
        setDate()

        binding.ivSettings.setOnClickListener {
            showLogoutDialog()
        }

//        binding.btnStartStudying.setOnClickListener {
//            findNavController().navigate(R.id.nav_study)
//        }

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
    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user == null) {
            // If for some reason there's no user
            logoutUser()
            return
        }

        // Get the user's document from the "users" collection in Firestore
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Document found, populate the UI
                    val displayName = document.getString("displayName") ?: "User"
                    val school = document.getString("school") ?: "N/A"
                    val course = document.getString("course") ?: "N/A"
                    val yearLevel = document.getLong("yearLevel")?.toString() ?: "N/A"
                    val profilePictureUrl = document.getString("profilePictureUrl")

                    // Set the greeting text
                    binding.tvGreeting.text = getString(R.string.hello_user, displayName)

                    // Set the profile card details
                    binding.profileCard.apply {
                        tvNameValue.text = displayName
                        tvSchoolValue.text = school
                        tvCourseValue.text = course
                        tvYearLevelValue.text = yearLevel
                    }

                    // Load the profile image using Glide
                    if (profilePictureUrl != null) {
                        Glide.with(this@HomeFragment)
                            .load(profilePictureUrl)
                            .circleCrop() // Make the image a circle
                            .placeholder(R.drawable.ic_add_photo) // Placeholder while loading
                            .error(R.drawable.ic_add_photo) // Image to show if loading fails
                            .into(binding.profileCard.ivProfileAvatar)
                    }

                } else {
                    Log.d("HomeFragment", "No such document for user: ${user.uid}")
                    // Todo: Handle case where profile doc is missing
                }
            }
            .addOnFailureListener { exception ->
                Log.d("HomeFragment", "get failed with ", exception)
                // Todo: Show an error message
            }
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
            Task("1", "Finals Exam", "timestamp", null, false, "needsAction", "June 28", "June 28", null, "STCLOUD", "timestamp"),
            Task("2", "MCO Presentation", "timestamp", null, false, "needsAction", "June 30", "June 30", null, "MOBICOM", "timestamp"),
            Task("3", "Review for Quiz 2", "timestamp", null, false, "needsAction", "July 02", "July 02", null, "CSARCH2", "timestamp"),
        )
    }

    private fun logoutUser() {
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).userDao().clearUser()
        }
        auth.signOut()
        // Intent to go back to the LandingActivity
        val intent = Intent(requireActivity(), LandingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish() // Finish the MainActivity
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // fragment lifecycle
    }
}