package com.mobicom.s17.group8.mobicom_mco.home

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
import androidx.core.net.toUri
import com.mobicom.s17.group8.mobicom_mco.database.user.User
import com.mobicom.s17.group8.mobicom_mco.database.user.UserDao
import android.widget.PopupMenu
import com.bumptech.glide.signature.ObjectKey
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedDate
import com.mobicom.s17.group8.mobicom_mco.utils.toFormattedTime
import java.io.File
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mobicom.s17.group8.mobicom_mco.auth.UserAuthViewModel
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskRepository


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var userDao: UserDao
    private val userAuthViewModel: UserAuthViewModel by activityViewModels()

    private val homeViewModel: HomeViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = TaskRepository(database.taskDao(), database.taskListDao())
        val userId = userAuthViewModel.currentUserId.value!!
        HomeViewModelFactory(repository, userId)
    }

    private lateinit var homeTaskAdapter: HomeTaskAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        userDao = AppDatabase.getDatabase(requireContext()).userDao()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        loadAndObserveUserProfile()
        setDate()

        binding.ivSettings.setOnClickListener { view ->
            showSettingsMenu(view)
        }

    }

    private fun loadAndObserveUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            logoutUser() // Should not happen, but safe to check
            return
        }

        userDao.getUserById(currentUser.uid).observe(viewLifecycleOwner) { userEntity ->
            if (userEntity != null) {
                binding.tvGreeting.text = getString(R.string.hello_user, userEntity.displayName ?: "User")

                binding.profileCard.apply {
                    tvNameValue.text = userEntity.displayName ?: "N/A"
                    tvSchoolValue.text = userEntity.school ?: "N/A"
                    tvCourseValue.text = userEntity.course ?: "N/A"
                    tvYearLevelValue.text = userEntity.yearLevel?.toString() ?: "N/A"
                }

                // Load the profile picture from the local URI
                if (userEntity.localProfilePictureUri != null) {
                    val imageUri = userEntity.localProfilePictureUri.toUri()
                    val imageFile = imageUri.path?.let { File(it) }

                    if (imageFile != null && imageFile.exists()) {
                        Glide.with(this@HomeFragment)
                            .load(imageUri)
                            .signature(ObjectKey(imageFile.lastModified()))
                            .placeholder(R.drawable.ic_add_photo)
                            .error(R.drawable.ic_add_photo)
                            .into(binding.profileCard.ivProfileAvatar)
                    } else {
                        binding.profileCard.ivProfileAvatar.setImageResource(R.drawable.ic_add_photo)
                    }
                } else {
                    binding.profileCard.ivProfileAvatar.setImageResource(R.drawable.ic_add_photo)
                }
            }
        }

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val existingUser = userDao.getNonLiveUserById(currentUser.uid)

                        val updatedUser = User(
                            uid = currentUser.uid,
                            email = currentUser.email,
                            displayName = document.getString("displayName"),
                            school = document.getString("school"),
                            course = document.getString("course"),
                            yearLevel = document.getLong("yearLevel")?.toInt(),
                            localProfilePictureUri = existingUser?.localProfilePictureUri
                        )
                        if (existingUser != null) {
                            userDao.updateUser(updatedUser)
                        } else {
                            userDao.insertOrUpdateUser(updatedUser)
                        }

                    }
                } else {
                    Log.d("HomeFragment", "No Firestore document for user: ${currentUser.uid}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("HomeFragment", "Error getting documents: ", exception)
            }
    }

    private fun setupRecyclerView() {
        homeTaskAdapter = HomeTaskAdapter()
        binding.rvHomeTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = homeTaskAdapter
        }
    }

    private fun observeViewModel() {
        homeViewModel.upcomingTasks.observe(viewLifecycleOwner) { tasks ->
            homeTaskAdapter.submitList(tasks)
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


    private fun showSettingsMenu(anchorView: View) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(R.menu.home_settings_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit_profile -> {
                    val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_logout -> {
                    showLogoutDialog()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun logoutUser() {
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