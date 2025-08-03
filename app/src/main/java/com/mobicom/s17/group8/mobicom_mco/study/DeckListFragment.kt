package com.mobicom.s17.group8.mobicom_mco.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentDeckListBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class DeckListFragment : Fragment() {
    private var _binding: FragmentDeckListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DeckAdapter

    private val viewModel: StudyViewModel by lazy {
        ViewModelProvider(requireActivity())[StudyViewModel::class.java]
    }

    private lateinit var studyViewModel: StudyViewModel

    private val args: DeckListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeckListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studyViewModel = ViewModelProvider(requireActivity())[StudyViewModel::class.java]

        adapter = DeckAdapter(
            studyViewModel = studyViewModel,
            onPlayClicked = { selectedDeck ->
                val action = DeckListFragmentDirections
                    .actionDeckListFragmentToFlashcardListFragment(selectedDeck.deckId, shuffle = false)
                findNavController().navigate(action)
            },
                    onRenameDeck = { updatedDeck ->
                studyViewModel.renameDeck(updatedDeck)
            },
            onDeleteDeck = { deckToDelete ->
                studyViewModel.deleteDeck(deckToDelete)
            }
        )

        binding.rvDecks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDecks.adapter = adapter

        val courseId = args.courseId
        binding.tvDeckTitle.text = args.courseName

        viewModel.getDecksForCourse(courseId).observe(viewLifecycleOwner) { decks ->
            adapter.submitList(decks)
        }

        binding.fabAddDeck.setOnClickListener {
            AddDeckDialogFragment(courseId = args.courseId) { newDeck ->
                studyViewModel.addDeck(newDeck)
            }.show(parentFragmentManager, "add_deck")
        }

        binding.fabPlayCourse.setOnClickListener {
            val decks = adapter.getCurrentList().filter { it.courseId == args.courseId }
            val flashcards = decks.flatMap { deck ->
                studyViewModel.getFlashcardsForDeckSync(deck.deckId)
            }.shuffled()

            if (flashcards.isEmpty()) {
                Toast.makeText(requireContext(), "No flashcards to play!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            studyViewModel.setTempFlashcards(flashcards)

            // Navigate with dummy deckId but set shuffle = true
            val action = DeckListFragmentDirections
                .actionDeckListFragmentToFlashcardListFragment(deckId = "dummy", shuffle = true)

            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
