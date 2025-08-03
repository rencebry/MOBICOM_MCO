package com.mobicom.s17.group8.mobicom_mco.study

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentFlashcardListBinding
import androidx.navigation.fragment.navArgs
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mobicom.s17.group8.mobicom_mco.R

class FlashcardListFragment : Fragment() {
    private var _binding: FragmentFlashcardListBinding? = null
    private val binding get() = _binding!!

    private lateinit var studyViewModel: StudyViewModel
    private lateinit var adapter: RecyclerView.Adapter<*>
    private val args: FlashcardListFragmentArgs by navArgs()

    private var showOnlyFavorites = false
    private var isCompressedView = false
    private var currentFlashcards: List<Flashcard> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFlashcardListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studyViewModel = ViewModelProvider(requireActivity())[StudyViewModel::class.java]

        val shouldShuffle = args.shuffle
        val isCourseShuffle = shouldShuffle && args.deckId == "dummy"
        val actualDeckId = args.deckId ?: return

        adapter = createAdapter(isCompressedView)
        binding.rvFlashcards.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFlashcards.adapter = adapter

        when (val currentAdapter = adapter) {
            is FlashcardAdapterCompressed -> currentAdapter.submitList(currentFlashcards)
            is FlashcardAdapterExpanded -> currentAdapter.submitList(currentFlashcards)
        }

        // Recycler View layout stuff
        val screenHeight = resources.displayMetrics.heightPixels
        val verticalPadding = (screenHeight * 0.25).toInt()  // 25% top/bottom padding
        binding.rvFlashcards.setPadding(0, verticalPadding, 0, verticalPadding/3)
        binding.rvFlashcards.clipToPadding = false

        // List animation
        binding.rvFlashcards.itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = false
        }

        // Filter favorite cards
        binding.btnFilterCards.setOnClickListener {
            showOnlyFavorites = !showOnlyFavorites

            binding.btnFilterCards.setImageResource(
                if (showOnlyFavorites) R.drawable.filter_off else R.drawable.filter
            )

            val visibleFavorites = currentFlashcards.count { it.isFavorite }

            (adapter as? FlashcardAdapterExpanded)?.setShowOnlyFavorites(showOnlyFavorites, currentFlashcards)
            (adapter as? FlashcardAdapterCompressed)?.setShowOnlyFavorites(showOnlyFavorites, currentFlashcards)

            binding.tvNoFavCards.visibility =
                if (showOnlyFavorites && visibleFavorites == 0) View.VISIBLE else View.GONE
        }

        // Observe/store flashcards for view switching
        if (isCourseShuffle) {
            studyViewModel.tempFlashcards.observe(viewLifecycleOwner) { flashcards ->
                if (flashcards.isNullOrEmpty()) return@observe

                currentFlashcards = flashcards

                when (val currentAdapter = adapter) {
                    is FlashcardAdapterExpanded -> {
                        currentAdapter.submitList(currentFlashcards)
                        if (isCourseShuffle) currentAdapter.resetAllCardsToFront()
                    }
                    is FlashcardAdapterCompressed -> {
                        currentAdapter.submitList(currentFlashcards)
                    }
                }
            }
        } else {
            studyViewModel.getFlashcardsForDeck(actualDeckId).observe(viewLifecycleOwner) { flashcardsForDeck ->
                val finalList = if (shouldShuffle) flashcardsForDeck.shuffled() else flashcardsForDeck
                currentFlashcards = flashcardsForDeck

                Log.d("FlashcardListFragment", "flashcardsForDeck: ${flashcardsForDeck.size}")

                when (val currentAdapter = adapter) {
                    is FlashcardAdapterExpanded -> {
                        currentAdapter.submitList(finalList)
                        if (shouldShuffle) currentAdapter.resetAllCardsToFront()
                    }
                    is FlashcardAdapterCompressed -> currentAdapter.submitList(finalList)
                }
            }
        }

        // View switching
        binding.viewSwitch.setOnClickListener {
            isCompressedView = !isCompressedView

            // Toggle icon
            binding.viewSwitch.setImageResource(
                if (isCompressedView) R.drawable.compressed else R.drawable.expanded
            )

            adapter = createAdapter(isCompressedView)
            binding.rvFlashcards.adapter = adapter

            when (val currentAdapter = adapter) {
                is FlashcardAdapterExpanded -> currentAdapter.submitList(currentFlashcards)
                is FlashcardAdapterCompressed -> currentAdapter.submitList(currentFlashcards)
            }
        }

        // View switch icon default state
        binding.viewSwitch.setImageResource(
            if (isCompressedView) R.drawable.compressed else R.drawable.expanded
        )

        if (!isCourseShuffle) {
            binding.fabAddCards.setOnClickListener {
                AddFlashcardDialogFragment(
                    deckId = actualDeckId,
                    courseId = studyViewModel.getCourseIdFromDeck(actualDeckId),
                ) { newFlashcard ->
                    studyViewModel.addFlashcard(actualDeckId, newFlashcard)
                }.show(parentFragmentManager, "add_flashcard")
            }
        } else {
            binding.fabAddCards.visibility = View.GONE
        }

        binding.fabPlayDeck.setOnClickListener {
            binding.fabPlayDeck.setOnClickListener {
                val shuffled = currentFlashcards.shuffled()

                // Flip to question side if in expanded view
                when (val currentAdapter = adapter) {
                    is FlashcardAdapterExpanded -> {
                        currentAdapter.submitList(shuffled)
                    }
                    is FlashcardAdapterCompressed -> {
                        currentAdapter.submitList(shuffled)
                    }
                }
            }
        }
    }

    private fun createAdapter(
        compressed: Boolean
    ): RecyclerView.Adapter<*> {
        return if (compressed) {
            FlashcardAdapterCompressed(
                { flashcardToDelete -> studyViewModel.deleteFlashcard(flashcardToDelete) },
                { updatedFlashcard -> studyViewModel.updateFlashcard(updatedFlashcard) },
                { updatedFlashcard -> studyViewModel.updateFlashcard(updatedFlashcard) },
                showOnlyFavorites
            )
        } else {
            FlashcardAdapterExpanded(
                { flashcardToDelete -> studyViewModel.deleteFlashcard(flashcardToDelete) },
                { updatedFlashcard -> studyViewModel.updateFlashcard(updatedFlashcard) },
                { updatedFlashcard -> studyViewModel.updateFlashcard(updatedFlashcard) },
                showOnlyFavorites
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
