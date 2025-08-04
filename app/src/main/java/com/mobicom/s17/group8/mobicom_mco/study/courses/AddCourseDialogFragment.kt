package com.mobicom.s17.group8.mobicom_mco.study.courses

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.databinding.DialogAddCourseBinding
import java.util.UUID
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.study.StudyRepository
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModelFactory


class AddCourseDialogFragment : DialogFragment() {

    private var _binding: DialogAddCourseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudyViewModel by lazy {
        val activity = requireActivity()
        val userId = Firebase.auth.currentUser?.uid ?: ""
        val database = AppDatabase.getDatabase(activity.applicationContext)
        val repository = StudyRepository(
            courseDao = database.courseDao(),
            deckDao = database.deckDao(),
            flashcardDao = database.flashcardDao(),
            userId = userId
        )
        val factory = StudyViewModelFactory(repository, userId)
        ViewModelProvider(activity, factory).get(StudyViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorPickers()

        binding.btnSave.setOnClickListener {
            val courseName = binding.etCourseTitle.text.toString().trim()
            if (courseName.isNotBlank()) {
                val selectedColorId = getSelectedColor()
                viewModel.addCourse(courseName, selectedColorId)
                dismiss()
            } else {
                // Show error
            }
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
            val colorCircle = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ContextCompat.getColor(requireContext(), colorResId))
            }

            val borderSelector =
                ContextCompat.getDrawable(requireContext(), R.drawable.color_picker_selector)

            val layers = arrayOf(colorCircle, borderSelector)
            val layerDrawable = LayerDrawable(layers)

            radioButton.background = layerDrawable
        }

        }

    private fun getSelectedColor(): Int {
        return when (binding.colorPickerGroup.checkedRadioButtonId) {
            R.id.colorGreen -> R.color.vinyl_green
            R.id.colorYellow -> R.color.vinyl_yellow
            R.id.colorOrange -> R.color.vinyl_orange
            R.id.colorPurple -> R.color.vinyl_purple
            R.id.colorMint -> R.color.vinyl_mint
            else -> R.color.vinyl_blue
        }
    }

    override fun onStart() {
        super.onStart()
        // dialog window
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }}
