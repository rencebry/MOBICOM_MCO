package com.mobicom.s17.group8.mobicom_mco.study.deck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentDeckListBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel

class DeckListFragment : Fragment() {
    private var _binding: FragmentDeckListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DeckAdapter

    private val viewModel: StudyViewModel by lazy {
        ViewModelProvider(requireActivity()).get(StudyViewModel::class.java)
    }

    private val args: DeckListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeckListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDeckTitle.text = args.courseName
        val courseId = args.courseId

        setupAdapter()
        setupRecyclerView()
        observeDecks(courseId)
        setupClickListeners(courseId)
    }

    private fun setupAdapter() {
        adapter = DeckAdapter(
            studyViewModel = viewModel,
            onPlayClicked = { selectedDeck ->
                // Navigate to the flashcard viewer screen (shuffle OFF)
                val action = DeckListFragmentDirections
                    .actionDeckListFragmentToFlashcardListFragment(selectedDeck.deckId, args.courseId, shuffle = false)
                findNavController().navigate(action)
            },
            onDeleteDeck = { deckToDelete ->
                // TODO: Show a confirmation dialog before deleting
                viewModel.deleteDeck(deckToDelete)
            }
        )
    }

    private fun setupRecyclerView() {
        binding.rvDecks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDecks.adapter = adapter
    }

    private fun observeDecks(courseId: String) {
        // Observe the LiveData from the ViewModel to get decks for this specific course
        viewModel.getDecksForCourse(courseId).observe(viewLifecycleOwner) { decks ->
            adapter.submitList(decks)
            // You can add empty state logic here
        }
    }

    private fun setupClickListeners(courseId: String) {
        binding.buttonBackToCourses.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fabAddDeck.setOnClickListener {
            AddDeckDialogFragment.newInstance(courseId)
                .show(parentFragmentManager, "add_deck")
        }

        binding.fabPlayCourse.setOnClickListener {
            val decks = adapter.currentList
            if (decks.isEmpty()) {
                Toast.makeText(requireContext(), "This course has no decks to play!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val action = DeckListFragmentDirections
                .actionDeckListFragmentToFlashcardListFragment(
                    deckId = "course_play_dummy",
                    courseId = courseId,
                    shuffle = true
                )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}