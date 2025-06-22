package com.mobicom.s17.group8.mobicom_mco

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobicom.s17.group8.mobicom_mco.databinding.ActivityLandingBinding

// run here
class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create Account Button
        binding.btnCreateAccount.setOnClickListener {
            // SignupActivity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Login Button
        binding.btnLogin.setOnClickListener {
            // LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}