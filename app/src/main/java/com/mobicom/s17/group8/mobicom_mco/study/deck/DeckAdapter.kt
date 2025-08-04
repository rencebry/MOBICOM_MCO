package com.mobicom.s17.group8.mobicom_mco.study.deck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.study.Deck
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemDeckBinding

class DeckAdapter(
    private val onAction: (Deck, DeckAction) -> Unit
) : RecyclerView.Adapter<DeckAdapter.DeckViewHolder>() {

    // Sealed class to represent all possible actions on a deck item
    sealed class DeckAction {
        object Click : DeckAction()
        object Play : DeckAction()
        object Favorite : DeckAction()
        object Rename : DeckAction()
        object Delete : DeckAction()
        object Export : DeckAction()
    }

    private var decks: List<Deck> = emptyList()

    inner class DeckViewHolder(val binding: ListItemDeckBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Set up click listeners once when the view is created for efficiency
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onAction(decks[adapterPosition], DeckAction.Click)
                }
            }
            binding.deckPlay.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onAction(decks[adapterPosition], DeckAction.Play)
                }
            }
            binding.favoriteDeck.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onAction(decks[adapterPosition], DeckAction.Favorite)
                }
            }
            binding.deckOptions.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showOptionsMenu(it as View, decks[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding = ListItemDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeckViewHolder(binding)
    }

    override fun getItemCount() = decks.size

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = decks[position]
        holder.binding.apply {
            deckTitleTv.text = deck.deckTitle
            deckInfoTv.text = "${deck.dateCreated} | ${deck.cardCount} flashcards"

             val starIcon = if (deck.isFavorite) R.drawable.ic_star_enabled else R.drawable.ic_star_disabled
             favoriteDeck.setImageResource(starIcon)
        }
    }

    private fun showOptionsMenu(anchorView: View, deck: Deck) {
        val popup = PopupMenu(anchorView.context, anchorView)
        popup.menuInflater.inflate(R.menu.deck_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.rename_deck -> {
                    onAction(deck, DeckAction.Rename)
                    true
                }
                R.id.delete_deck -> {
                    onAction(deck, DeckAction.Delete)
                    true
                }
                R.id.export_deck_txt -> {
                    onAction(deck, DeckAction.Export)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    fun submitList(newDecks: List<Deck>) {
        decks = newDecks
        notifyDataSetChanged()
    }
}