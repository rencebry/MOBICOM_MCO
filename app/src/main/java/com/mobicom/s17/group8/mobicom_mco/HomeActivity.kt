package com.mobicom.s17.group8.mobicom_mco

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
    }
}