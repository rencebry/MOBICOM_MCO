package com.mobicom.s17.group8.mobicom_mco.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // fragment lifecycle
    }
}