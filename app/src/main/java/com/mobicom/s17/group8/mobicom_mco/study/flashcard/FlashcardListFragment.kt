package com.mobicom.s17.group8.mobicom_mco.study.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.study.Deck
import com.mobicom.s17.group8.mobicom_mco.database.study.Flashcard
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentFlashcardListBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel
import kotlinx.coroutines.launch

class FlashcardListFragment : Fragment() {
    private var _binding: FragmentFlashcardListBinding? = null
    private val binding get() = _binding!!

    private val args: FlashcardListFragmentArgs by navArgs()
    private val studyViewModel: StudyViewModel by lazy {
        ViewModelProvider(requireActivity()).get(StudyViewModel::class.java)
    }

    private lateinit var adapter: RecyclerView.Adapter<*>
    private var currentDeck: Deck? = null
    private var currentFlashcards: List<Flashcard> = emptyList()
    private var showOnlyFavorites = false
    private var isCompressedView = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFlashcardListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deckId = args.deckId
        if (deckId == null) {
            Toast.makeText(requireContext(), "Error: Deck ID not found.", Toast.LENGTH_SHORT).show()
            return // Stop execution if there's no ID
        }

        viewLifecycleOwner.lifecycleScope.launch {
            currentDeck = studyViewModel.getDeckById(deckId)

            if (currentDeck != null) {
                (activity as? AppCompatActivity)?.supportActionBar?.title = currentDeck?.deckTitle

                setupViews()
                setupClickListeners()
                observeData()
            } else {
                Toast.makeText(requireContext(), "Deck not found.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViews() {
        adapter = createAdapter(isCompressedView)
        binding.rvFlashcards.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFlashcards.adapter = adapter
        binding.rvFlashcards.itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = false
        }

        val screenHeight = resources.displayMetrics.heightPixels
        val verticalPadding = (screenHeight * 0.25).toInt()
        binding.rvFlashcards.setPadding(0, verticalPadding, 0, verticalPadding / 3)
        binding.rvFlashcards.clipToPadding = false

        binding.btnFilterCards.setImageResource(if (showOnlyFavorites) R.drawable.filter_off else R.drawable.filter)
        binding.viewSwitch.setImageResource(if (isCompressedView) R.drawable.expanded else R.drawable.compressed)
    }

    private fun observeData() {
        val deckId = args.deckId!!
        studyViewModel.getFlashcardsForDeck(deckId).observe(viewLifecycleOwner) { flashcardsFromDb ->
            currentFlashcards = flashcardsFromDb

            val initialList = if (args.shuffle) {
                currentFlashcards.shuffled()
            } else {
                currentFlashcards
            }
            updateAdapterWithList(initialList)
        }
    }

    private fun setupClickListeners() {
        val deckId = args.deckId!! // Safe to use !! here
        val isCourseShuffle = args.shuffle && deckId == "course_play_dummy"

        if (!isCourseShuffle) {
            binding.fabAddCards.setOnClickListener {
                val courseId = currentDeck?.courseId
                if (courseId != null) {
                    AddFlashcardDialogFragment.newInstance(deckId, courseId)
                        .show(parentFragmentManager, "add_flashcard")
                } else {
                    Toast.makeText(requireContext(), "Could not determine course.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.fabAddCards.visibility = View.GONE
        }

        binding.btnFilterCards.setOnClickListener {
            showOnlyFavorites = !showOnlyFavorites
            binding.btnFilterCards.setImageResource(if (showOnlyFavorites) R.drawable.filter_off else R.drawable.filter)

            val visibleFavorites = currentFlashcards.count { it.isFavorite }
            binding.tvNoFavCards.visibility = if (showOnlyFavorites && visibleFavorites == 0) View.VISIBLE else View.GONE

            updateAdapterWithList(currentFlashcards)
        }

        binding.viewSwitch.setOnClickListener {
            isCompressedView = !isCompressedView
            binding.viewSwitch.setImageResource(if (isCompressedView) R.drawable.compressed else R.drawable.expanded)

            adapter = createAdapter(isCompressedView)
            binding.rvFlashcards.adapter = adapter
            updateAdapterWithList(currentFlashcards)
        }

        binding.fabPlayDeck.setOnClickListener {
            currentFlashcards = currentFlashcards.shuffled()
            updateAdapterWithList(currentFlashcards)
        }
    }

    private fun createAdapter(compressed: Boolean): RecyclerView.Adapter<*> {
        return if (compressed) {
            FlashcardAdapterCompressed(
                onDeleteFlashcard = { flashcard -> studyViewModel.deleteFlashcard(flashcard) },
                onEditFlashcard = { flashcard ->
                    EditFlashcardDialogFragment.newInstance(flashcard.flashcardId)
                        .show(parentFragmentManager, "edit_flashcard")
                },
                onUpdateFlashcard = { flashcard -> studyViewModel.updateFlashcard(flashcard) }
            )
        } else {
            FlashcardAdapterExpanded(
                onDeleteFlashcard = { flashcard -> studyViewModel.deleteFlashcard(flashcard) },
                onEditFlashcard = { flashcard ->
                    EditFlashcardDialogFragment.newInstance(flashcard.flashcardId)
                        .show(parentFragmentManager, "edit_flashcard")
                },
                onUpdateFlashcard = { flashcard -> studyViewModel.updateFlashcard(flashcard) }
            )
        }
    }

    private fun updateAdapterWithList(list: List<Flashcard>) {
        when (val currentAdapter = adapter) {
            is FlashcardAdapterExpanded -> {
                currentAdapter.setShowOnlyFavorites(showOnlyFavorites, list)
                currentAdapter.resetAllCardsToFront()
            }
            is FlashcardAdapterCompressed -> {
                currentAdapter.setShowOnlyFavorites(showOnlyFavorites, list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}