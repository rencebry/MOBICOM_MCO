package com.mobicom.s17.group8.mobicom_mco.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.database.user.User
import com.mobicom.s17.group8.mobicom_mco.databinding.ActivityCreateProfileBinding
import com.mobicom.s17.group8.mobicom_mco.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage
    private var imageUri: Uri? = null

    // Launcher for getting an image from the gallery
    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.ivProfilePicture.setImageURI(it)
                imageUri = it
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pre-fill the display name from the Google account
        binding.etDisplayName.setText(auth.currentUser?.displayName ?: "")

        binding.ivProfilePicture.setOnClickListener {
            // Launch the image picker
            pickImageLauncher.launch("image/*")
        }

        binding.btnCompleteProfile.setOnClickListener {
            if (validateInput()) {
                if (imageUri != null) {
                    uploadProfilePictureAndSaveData()
                } else {
                    saveUserProfile(null)
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        if (binding.etDisplayName.text.isBlank() ||
            binding.etSchool.text.isBlank() ||
            binding.etCourse.text.isBlank() ||
            binding.etYearLevel.text.isBlank()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return false
        }
//        if (imageUri == null) {
//            Toast.makeText(this, "Please select a profile picture.", Toast.LENGTH_SHORT).show()
//            return false
//        }
        return true
    }

    private fun uploadProfilePictureAndSaveData() {
        val user = auth.currentUser ?: return
        val filename = UUID.randomUUID().toString()
        val storageRef = storage.reference.child("profile_pictures/$filename")

        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        // After successful upload, save the profile with the new URL
                        saveUserProfile(downloadUrl.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserProfile(profilePictureUrl: String?) {
        val user = auth.currentUser!!
        val displayName = binding.etDisplayName.text.toString().trim()
        val school = binding.etSchool.text.toString().trim()
        val course = binding.etCourse.text.toString().trim()
        val yearLevel = binding.etYearLevel.text.toString().trim().toIntOrNull()

        val userProfile = hashMapOf(
            "uid" to user.uid,
            "email" to user.email,
            "displayName" to binding.etDisplayName.text.toString().trim(),
            "school" to binding.etSchool.text.toString().trim(),
            "course" to binding.etCourse.text.toString().trim(),
            "yearLevel" to binding.etYearLevel.text.toString().trim().toInt(),
            "profilePictureUrl" to profilePictureUrl,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(user.uid)
            .set(userProfile)
            .addOnSuccessListener {
                val roomUser = User(
                    uid = user.uid,
                    email = user.email,
                    displayName = displayName,
                    school = school,
                    course = course,
                    yearLevel = yearLevel,
                    profilePictureUrl = profilePictureUrl
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    val userDao = AppDatabase.getDatabase(applicationContext).userDao()
                    userDao.insertOrUpdateUser(roomUser)
                }
                Toast.makeText(this, "Profile created successfully!", Toast.LENGTH_SHORT).show()
                navigateToMainApp()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}