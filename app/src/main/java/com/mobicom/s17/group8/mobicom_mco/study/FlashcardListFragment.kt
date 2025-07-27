package com.mobicom.s17.group8.mobicom_mco.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentFlashcardListBinding
import androidx.navigation.fragment.navArgs

class FlashcardListFragment : Fragment() {

    private var _binding: FragmentFlashcardListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FlashcardAdapter
    private val args: FlashcardListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFlashcardListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sample full list of flashcards
        val allFlashcards = listOf(
            Flashcard("1", "course1","deck1", "What is Android?", "An OS."),
            Flashcard("2", "course1","deck1", "What is Kotlin?", "A modern language."),
            Flashcard("3", "course1","deck2", "What is a Fragment?", "A modular UI block.")
        )

        // Get deckId from Safe Args
        val deckId = args.deckId

        // Filter flashcards for this deck
        val flashcardsForDeck = allFlashcards.filter { it.deckId == deckId }

        // Show in RecyclerView
        adapter = FlashcardAdapter(flashcardsForDeck)
        binding.rvFlashcards.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFlashcards.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}