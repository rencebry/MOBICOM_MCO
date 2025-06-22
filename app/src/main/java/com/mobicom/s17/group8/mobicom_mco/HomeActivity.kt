package com.mobicom.s17.group8.mobicom_mco

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobicom.s17.group8.mobicom_mco.databinding.ActivityHomeBinding // Updated import

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* TODO: home screen logic */
        val userName = "Kyoka" // place holder for now (to do)
        binding.tvGreeting.text = getString(R.string.hello_user, userName)
        binding.tvDate.text = "June 13th" //placeholder for now (to do)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // Navigate to Music
                R.id.nav_music -> {
                    val intent = Intent(this, MusicActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }

                R.id.nav_home -> {
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}

