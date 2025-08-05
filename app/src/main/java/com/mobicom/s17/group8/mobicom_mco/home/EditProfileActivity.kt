package com.mobicom.s17.group8.mobicom_mco.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.database.user.User
import com.mobicom.s17.group8.mobicom_mco.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import com.bumptech.glide.signature.ObjectKey

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var newImageUri: Uri? = null // To track if a new image was selected

    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                newImageUri = it
                binding.ivProfilePicture.setImageURI(it) // Show preview of new image
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCurrentProfile()

        binding.ivProfilePicture.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveChanges.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun loadCurrentProfile() {
        val user = auth.currentUser ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = AppDatabase.getDatabase(applicationContext).userDao()
            val localUser = userDao.getNonLiveUserById(user.uid)
            val firestoreDocument = db.collection("users").document(user.uid).get().await()

            withContext(Dispatchers.Main) {
                if (firestoreDocument.exists()) {
                    binding.etDisplayName.setText(firestoreDocument.getString("displayName"))
                    binding.etSchool.setText(firestoreDocument.getString("school"))
                    binding.etCourse.setText(firestoreDocument.getString("course"))
                    binding.etYearLevel.setText(firestoreDocument.getLong("yearLevel")?.toString() ?: "")

                    if (localUser?.localProfilePictureUri != null) {
                        val imageUri = localUser.localProfilePictureUri.toUri()
                        val imagePath = imageUri.path

                        if (imagePath != null) {
                            val imageFile = File(imagePath)
                            if (imageFile.exists()) {
                                Glide.with(this@EditProfileActivity)
                                    .load(imageUri)
                                    .signature(ObjectKey(imageFile.lastModified()))
                                    .placeholder(R.drawable.ic_add_photo)
                                    .error(R.drawable.ic_add_photo)
                                    .into(binding.ivProfilePicture)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Could not load profile.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun updateUserProfile() {
        val user = auth.currentUser ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = AppDatabase.getDatabase(applicationContext).userDao()
            val existingUser = userDao.getNonLiveUserById(user.uid)

            // Start with the existing image URI from the database
            var finalLocalImageUri: String? = existingUser?.localProfilePictureUri

            if (newImageUri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(newImageUri!!)
                    val file = File(filesDir, "${user.uid}_profile.jpg") // Overwrite the old file

                    // Use .use to automatically close the streams
                    FileOutputStream(file).use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }

                    // Update our final URI to the new file's path
                    finalLocalImageUri = Uri.fromFile(file).toString()
                    Log.d("EditProfileActivity", "New image saved locally to: $finalLocalImageUri")

                } catch (e: Exception) {
                    Log.e("EditProfileActivity", "Failed to save new image locally", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditProfileActivity, "Error saving new image.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
            }

            val updatedDisplayName = binding.etDisplayName.text.toString().trim()
            val updatedSchool = binding.etSchool.text.toString().trim()
            val updatedCourse = binding.etCourse.text.toString().trim()
            val updatedYearLevel = binding.etYearLevel.text.toString().trim().toIntOrNull()

            val firestoreUpdate = mapOf(
                "displayName" to updatedDisplayName,
                "school" to updatedSchool,
                "course" to updatedCourse,
                "yearLevel" to updatedYearLevel
            )

            val updatedRoomUser = User(
                uid = user.uid,
                email = user.email,
                displayName = updatedDisplayName,
                school = updatedSchool,
                course = updatedCourse,
                yearLevel = updatedYearLevel,
                localProfilePictureUri = finalLocalImageUri
            )

            try {
                db.collection("users").document(user.uid).update(firestoreUpdate).await()
                Log.d("EditProfileActivity", "Firestore updated successfully.")

                userDao.updateUser(updatedRoomUser)
                Log.d("EditProfileActivity", "Room database updated successfully.")

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditProfileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                    finish() // Go back to the home screen
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("EditProfileActivity", "Update failed", e)
                    Toast.makeText(this@EditProfileActivity, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}