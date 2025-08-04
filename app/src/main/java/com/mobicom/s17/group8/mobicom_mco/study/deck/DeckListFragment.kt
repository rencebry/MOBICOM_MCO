package com.mobicom.s17.group8.mobicom_mco.study.deck // Or your correct package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentDeckListBinding // Make sure this is your correct binding
import com.mobicom.s17.group8.mobicom_mco.study.StudyRepository
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.database.study.Deck

class DeckListFragment : Fragment() {
    private var _binding: FragmentDeckListBinding? = null
    private val binding get() = _binding!!


    private val args: DeckListFragmentArgs by navArgs()

    private val viewModel: StudyViewModel by viewModels {
        val activity = requireActivity()
        val userId = Firebase.auth.currentUser?.uid ?: ""
        val database = AppDatabase.getDatabase(activity.applicationContext)
        val repository = StudyRepository(
            courseDao = database.courseDao(),
            deckDao = database.deckDao(),
            flashcardDao = database.flashcardDao(),
            userId = userId
        )
        StudyViewModelFactory(repository, userId)
    }

    private lateinit var deckAdapter: DeckAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeckListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = args.courseName

        setupRecyclerView()
        setupClickListeners()

        observeAndRefreshDecks()
    }

    private fun setupRecyclerView() {
        deckAdapter = DeckAdapter { deck, action ->
            handleDeckAction(deck, action)
        }
        binding.rvDecks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDecks.adapter = deckAdapter
    }

    private fun setupClickListeners() {
        binding.fabAddDeck.setOnClickListener { // Use your FAB ID
            AddDeckDialogFragment.newInstance(args.courseId).show(parentFragmentManager, "AddDeckDialog")
        }
    }

    private fun observeAndRefreshDecks() {
        viewModel.refreshDecksForCourse(args.courseId)

        viewModel.getDecksForCourse(args.courseId).observe(viewLifecycleOwner) { decks ->
            if (decks != null) {
                deckAdapter.submitList(decks)
            }
        }
    }

    private fun handleDeckAction(deck: Deck, action: DeckAdapter.DeckAction) {
        when (action) {
            is DeckAdapter.DeckAction.Click -> {
                val navAction =
                    DeckListFragmentDirections.actionDeckListFragmentToFlashcardListFragment(deck.deckId)
                findNavController().navigate(navAction)
            }

            is DeckAdapter.DeckAction.Play -> {
                Toast.makeText(context, "Play deck: ${deck.deckTitle}", Toast.LENGTH_SHORT).show()
                val navAction =
                    DeckListFragmentDirections.actionDeckListFragmentToFlashcardListFragment(deck.deckId)
                findNavController().navigate(navAction)
            }

            is DeckAdapter.DeckAction.Favorite -> {
                Toast.makeText(
                    context,
                    "Favorite toggled for: ${deck.deckTitle}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is DeckAdapter.DeckAction.Rename -> {
                RenameDeckDialogFragment.newInstance(deck.deckId)
                    .show(parentFragmentManager, "RenameDeckDialog")
            }

            is DeckAdapter.DeckAction.Delete -> {
                viewModel.deleteDeck(deck)
            }

            is DeckAdapter.DeckAction.Export -> {
                Toast.makeText(context, "Export deck: ${deck.deckTitle}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}