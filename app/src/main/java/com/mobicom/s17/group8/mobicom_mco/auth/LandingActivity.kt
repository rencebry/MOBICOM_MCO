package com.mobicom.s17.group8.mobicom_mco.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.ActivityLandingBinding
import com.mobicom.s17.group8.mobicom_mco.home.MainActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.android.gms.common.api.Scope
import com.google.api.services.tasks.TasksScopes
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task


class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    companion object {
        private val TASKS_API_SCOPE = Scope(TasksScopes.TASKS)
    }

    private val googleSignInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("LandingActivity", "firebaseAuthWithGoogle:${account.id}")
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w("LandingActivity", "Google sign in failed", e)
                    showError("Google sign-in failed.")
                }
            } else {
                showError("Google sign-in was cancelled.")
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // If user is already signed in, go directly to the main app
        if (auth.currentUser != null) {
            navigateToMainApp()
            return
        }

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestScopes(TASKS_API_SCOPE)
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up button click listeners
        binding.btnGoogleSignIn.setOnClickListener {
            val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this)
            if (lastSignedInAccount != null && lastSignedInAccount.grantedScopes.contains(TASKS_API_SCOPE)){
                Log.d("LandingActivity", "User already granted scope, proceeding to Firebase auth.")
                firebaseAuthWithGoogle(lastSignedInAccount.idToken!!)
            } else {
                Log.d("LandingActivity", "Launching sign-in intent to request scope.")
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        binding.btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    checkIfNewUserAndNavigate(user.uid)
                } else {
                    Log.w("LandingActivity", "signInWithCredential:failure", task.exception)
                    showError("Firebase Authentication Failed.")
                }
            }
    }

    private fun checkIfNewUserAndNavigate(userId: String) {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User profile already exists, they are a returning user
                    Log.d("LandingActivity", "Returning user, navigating to main app.")
                    navigateToMainApp()
                } else {
                    // No profile found, this is a new user
                    Log.d("LandingActivity", "New user, navigating to create profile.")
                    navigateToCreateProfile()
                }
            }
            .addOnFailureListener { e ->
                // Handle the error, maybe default to creating a profile
                Log.e("LandingActivity", "Error checking for user profile", e)
                showError("Could not verify profile. Please try again.")
            }
    }

    private fun navigateToCreateProfile() {
        val intent = Intent(this, CreateProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}