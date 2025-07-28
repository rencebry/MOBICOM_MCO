package com.mobicom.s17.group8.mobicom_mco.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentDecksBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mobicom.s17.group8.mobicom_mco.study.DeckListFragmentDirections

class DeckListFragment : Fragment() {

    private var _binding: FragmentDecksBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DeckAdapter

    private val viewModel: StudyViewModel by lazy {
        ViewModelProvider(requireActivity()).get(StudyViewModel::class.java)
    }

    private val args: DeckListFragmentArgs by navArgs()

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
            val action = DeckListFragmentDirections
                .actionDeckListFragmentToFlashcardListFragment(selectedDeck.deckId)
            findNavController().navigate(action)
        }

        binding.rvDecks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDecks.adapter = adapter

        val courseId = args.courseId
        binding.tvDeckTitle.text = args.courseName

        viewModel.getDecksForCourse(courseId).observe(viewLifecycleOwner) { decks ->
            adapter.submitList(decks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}