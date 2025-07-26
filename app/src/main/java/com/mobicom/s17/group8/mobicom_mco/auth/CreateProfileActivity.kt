package com.mobicom.s17.group8.mobicom_mco.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.mobicom.s17.group8.mobicom_mco.home.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage
    private var imageUri: Uri? = null

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
                saveUserProfile()
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
        return true
    }

    private fun saveUserProfile() {
        val user = auth.currentUser ?: return

        // launches a coroutine for the entire save process
        lifecycleScope.launch(Dispatchers.IO) {
            var localImageUriString: String? = null

            // Copy image to internal storage
            if (imageUri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    // Create a file in the app's private directory
                    val file = File(filesDir, "${user.uid}_profile.jpg")
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                    // Get a permanent URI for our new local file
                    localImageUriString = Uri.fromFile(file).toString()
                    Log.d("CreateProfileActivity", "Image saved locally to: $localImageUriString")
                } catch (e: Exception) {
                    Log.e("CreateProfileActivity", "Failed to save image locally", e)
                }
            }

            val firestoreProfile = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "displayName" to binding.etDisplayName.text.toString().trim(),
                "school" to binding.etSchool.text.toString().trim(),
                "course" to binding.etCourse.text.toString().trim(),
                "yearLevel" to binding.etYearLevel.text.toString().trim().toIntOrNull(),
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("users").document(user.uid).set(firestoreProfile)
                .addOnSuccessListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val roomUser = User(
                            uid = user.uid,
                            email = user.email,
                            displayName = binding.etDisplayName.text.toString().trim(),
                            school = binding.etSchool.text.toString().trim(),
                            course = binding.etCourse.text.toString().trim(),
                            yearLevel = binding.etYearLevel.text.toString().trim().toIntOrNull(),
                            localProfilePictureUri = localImageUriString // Save the local URI here
                        )

                        val userDao = AppDatabase.getDatabase(applicationContext).userDao()
                        userDao.insertOrUpdateUser(roomUser)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CreateProfileActivity, "Profile created successfully!", Toast.LENGTH_SHORT).show()
                            navigateToMainApp()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Switch back to main thread to show Toast
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(this@CreateProfileActivity, "Failed to create profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}