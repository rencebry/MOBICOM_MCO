package com.mobicom.s17.group8.mobicom_mco.study

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemDeckBinding
import com.mobicom.s17.group8.mobicom_mco.R

class DeckAdapter(
    private val studyViewModel: StudyViewModel,
    private val onPlayClicked: (Deck) -> Unit,
    private val onRenameDeck: (Deck) -> Unit,
    private val onDeleteDeck: (Deck) -> Unit
) : RecyclerView.Adapter<DeckAdapter.DeckViewHolder>() {

    private var deckList: List<Deck> = emptyList()

    fun submitList(decks: List<Deck>) {
        deckList = decks
        notifyDataSetChanged()
    }

    fun getCurrentList(): List<Deck> = deckList

    inner class DeckViewHolder(val binding: ListItemDeckBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(deck: Deck) {
            binding.deckTitleTv.text = deck.deckTitle
            val countText = binding.root.context.getString(R.string.deck_stat, deck.dateCreated, deck.cardCount)
            binding.deckInfoTv.text = countText

            // Favorite button
            binding.favoriteDeck.setImageResource(
                if (deck.isFavorite) R.drawable.ic_star_enabled else R.drawable.ic_star_disabled
            )

            binding.favoriteDeck.setOnClickListener {
                val newFavoriteState = !deck.isFavorite

                // Update UI immediately
                binding.favoriteDeck.setImageResource(
                    if (newFavoriteState) R.drawable.ic_star_enabled else R.drawable.ic_star_disabled
                )

                // TEMPORARY BLOCK: Update in-memory deck state
                deckList = deckList.map {
                    if (it.deckId == deck.deckId) it.copy(isFavorite = newFavoriteState) else it
                }
                notifyItemChanged(adapterPosition)

                // TO-IMPLEMENT: Update deck state, persist deck state
                // val updatedDeck = deck.copy(isFavorite = newFavoriteState)
                // studyViewModel.updateDeck(updatedDeck)

                // Update all flashcards
                val flashcardsInDeck = studyViewModel.getFlashcardsForDeckSync(deck.deckId)
                val updatedFlashcards = flashcardsInDeck.map {
                    it.copy(isFavorite = newFavoriteState)
                }

                updatedFlashcards.forEach { flashcard ->
                    studyViewModel.updateFlashcard(flashcard)
                }
            }

            // Shuffle play button
            binding.deckPlay.setOnClickListener {
                onPlayClicked(deck)
            }

            // Flashcard pop-up options
            binding.deckOptions.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.inflate(R.menu.deck_menu)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.rename_deck -> {
                            RenameDeckDialogFragment(deck, onRenameDeck)
                                .show((view.context as androidx.fragment.app.FragmentActivity).supportFragmentManager, "rename_deck")
                            true
                        }

                        R.id.delete_deck -> {
                            // Play fade animation first
                            val fadeOut = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)

                            binding.root.startAnimation(fadeOut)

                            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {
                                    onDeleteDeck(deck)
                                }

                                override fun onAnimationEnd(animation: Animation?) {
                                }

                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                            true
                        }

                        R.id.export_deck_txt -> {
                            val flashcards = studyViewModel.getFlashcardsForDeckSync(deck.deckId)
                            val formattedText = flashcards.mapIndexed { index, card ->
                                "Q${index + 1}: ${card.question}\nA${index + 1}: ${card.answer}"
                            }.joinToString("\n\n")

                            val clipboard = view.context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Deck Flashcards", formattedText)
                            clipboard.setPrimaryClip(clip)
                            true
                        }

                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDeckBinding.inflate(inflater, parent, false)
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = deckList[position]
        holder.bind(deck)
    }

    override fun getItemCount(): Int = deckList.size

}
