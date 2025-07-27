package com.mobicom.s17.group8.mobicom_mco.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentDecksBinding
import androidx.navigation.fragment.findNavController
import com.mobicom.s17.group8.mobicom_mco.study.DeckListFragmentDirections

class DeckListFragment : Fragment() {

    private var _binding: FragmentDecksBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DeckAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDecksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DeckAdapter { selectedDeck ->
            // response to deckPlay click
            val action = DeckListFragmentDirections
                .actionDeckListFragmentToFlashcardListFragment(selectedDeck.deckId)
            findNavController().navigate(action)
        }

        binding.rvDecks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDecks.adapter = adapter

        // Temporary data for testing
        val sampleDecks = listOf(
            Deck("course1","deck1", "Android Basics", 10),
            Deck("course1","deck2", "Kotlin Fundamentals", 8),
            Deck("course1","deck3", "Jetpack Compose", 12)
        )

        adapter.submitList(sampleDecks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}