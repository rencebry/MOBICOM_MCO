package com.mobicom.s17.group8.mobicom_mco.study.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.AppDatabase
import com.mobicom.s17.group8.mobicom_mco.database.study.Deck
import com.mobicom.s17.group8.mobicom_mco.database.study.Flashcard
import com.mobicom.s17.group8.mobicom_mco.databinding.FragmentFlashcardListBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyRepository
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModelFactory
import kotlinx.coroutines.launch

class FlashcardListFragment : Fragment() {
    private var _binding: FragmentFlashcardListBinding? = null
    private val binding get() = _binding!!

    private val args: FlashcardListFragmentArgs by navArgs()
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

        viewLifecycleOwner.lifecycleScope.launch {
            currentDeck = args.deckId?.let { viewModel.getDeckById(it) }

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

    private fun observeData() {
        args.deckId?.let { viewModel.refreshFlashcardsForDeck(it) }

        args.deckId?.let {
            viewModel.getFlashcardsForDeck(it).observe(viewLifecycleOwner) { flashcardsFromDb ->
                currentFlashcards = flashcardsFromDb ?: emptyList()

                val initialList = if (args.shuffle) {
                    currentFlashcards.shuffled()
                } else {
                    currentFlashcards
                }
                updateAdapterWithList(initialList)
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


    private fun setupClickListeners() {
        val isCourseShuffle = args.shuffle && args.deckId == "course_play_dummy"

        if (!isCourseShuffle) {
            binding.fabAddCards.setOnClickListener {
                val courseId = currentDeck?.courseId
                if (courseId != null) {
                    args.deckId?.let { it1 ->
                        AddFlashcardDialogFragment.newInstance(it1, courseId)
                            .show(parentFragmentManager, "add_flashcard")
                    }
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
                onDeleteFlashcard = { flashcard -> viewModel.deleteFlashcard(flashcard) },
                onEditFlashcard = { flashcard ->
                    EditFlashcardDialogFragment.newInstance(flashcard.flashcardId)
                        .show(parentFragmentManager, "edit_flashcard")
                },
                onUpdateFlashcard = { flashcard -> viewModel.updateFlashcard(flashcard) }
            )
        } else {
            FlashcardAdapterExpanded(
                onDeleteFlashcard = { flashcard -> viewModel.deleteFlashcard(flashcard) },
                onEditFlashcard = { flashcard ->
                    EditFlashcardDialogFragment.newInstance(flashcard.flashcardId)
                        .show(parentFragmentManager, "edit_flashcard")
                },
                onUpdateFlashcard = { flashcard -> viewModel.updateFlashcard(flashcard) }
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