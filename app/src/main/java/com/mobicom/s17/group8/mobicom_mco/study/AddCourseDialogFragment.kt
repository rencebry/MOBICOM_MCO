package com.mobicom.s17.group8.mobicom_mco.study

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddCourseBinding
import java.util.UUID
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable


class AddCourseDialogFragment : DialogFragment() {

    private var _binding: DialogAddCourseBinding? = null
    private val binding get() = _binding!!

    // Get a reference to the same ViewModel the fragment is using
    private val viewModel: StudyViewModel by lazy {
        ViewModelProvider(requireActivity()).get(StudyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorPickers()

        binding.btnSave.setOnClickListener {
            val courseName = binding.etCourseName.text.toString()

            val selectedColorId = getSelectedColor()
            val newCourse = Course(
                id = UUID.randomUUID().toString(),
                name = courseName,
                deckCount = 0, // New courses start with 0 decks
                colorResId = selectedColorId
            )

            viewModel.addCourse(newCourse)
            dismiss()
        }


    }

    private fun setupColorPickers() {
        val colorMap = mapOf(
            binding.colorBlue to R.color.vinyl_blue,
            binding.colorGreen to R.color.vinyl_green,
            binding.colorYellow to R.color.vinyl_yellow,
            binding.colorOrange to R.color.vinyl_orange,
            binding.colorPurple to R.color.vinyl_purple,
            binding.colorMint to R.color.vinyl_mint
        )

        for ((radioButton, colorResId) in colorMap) {
            val color = ContextCompat.getColor(requireContext(), colorResId)
            radioButton.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun getSelectedColor(): Int {
        return when (binding.colorPickerGroup.checkedRadioButtonId) {
            R.id.colorGreen -> R.color.vinyl_green
            R.id.colorYellow -> R.color.vinyl_yellow
            R.id.colorOrange -> R.color.vinyl_orange
            R.id.colorPurple -> R.color.vinyl_purple
            R.id.colorMint -> R.color.vinyl_mint
            else -> R.color.vinyl_blue // Default
        }
    }

    override fun onStart() {
        super.onStart()
        // dialog window
        dialog?.window?.apply {

            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }}