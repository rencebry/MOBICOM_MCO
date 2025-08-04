package com.mobicom.s17.group8.mobicom_mco.study.deck

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.R
import com.mobicom.s17.group8.mobicom_mco.database.study.Deck
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemDeckBinding
import com.mobicom.s17.group8.mobicom_mco.study.StudyViewModel
import kotlinx.coroutines.launch

class DeckAdapter(
    private val studyViewModel: StudyViewModel,
    private val onPlayClicked: (Deck) -> Unit,
    private val onDeleteDeck: (Deck) -> Unit
) : ListAdapter<Deck, DeckAdapter.DeckViewHolder>(DeckDiffCallback()) {

    inner class DeckViewHolder(val binding: ListItemDeckBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deck: Deck) {
            binding.deckTitleTv.text = deck.deckTitle
            val countText = binding.root.context.getString(R.string.deck_stat, deck.dateCreated, deck.cardCount)
            binding.deckInfoTv.text = countText

            binding.favoriteDeck.setImageResource(
                if (deck.isFavorite) R.drawable.ic_star_enabled else R.drawable.ic_star_disabled
            )

            // --- LISTENERS ---

            binding.favoriteDeck.setOnClickListener {
                val updatedDeck = deck.copy(isFavorite = !deck.isFavorite)
                studyViewModel.updateDeck(updatedDeck)
            }

            binding.deckPlay.setOnClickListener { onPlayClicked(deck) }

            binding.deckOptions.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.inflate(R.menu.deck_menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.rename_deck -> {
                            RenameDeckDialogFragment.newInstance(deck.deckId)
                                .show((view.context as FragmentActivity).supportFragmentManager, "rename_deck")
                            true
                        }
                        R.id.delete_deck -> {
                            val fadeOut = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)
                            binding.root.startAnimation(fadeOut)
                            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(p0: Animation?) { onDeleteDeck(deck) }
                                override fun onAnimationEnd(p0: Animation?) {}
                                override fun onAnimationRepeat(p0: Animation?) {}
                            })
                            true
                        }
                        R.id.export_deck_txt -> {
                            view.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                                val flashcards = studyViewModel.getFlashcardsForExport(deck.deckId)
                                val formattedText = flashcards.joinToString("\n\n") { "Q: ${it.question}\nA: ${it.answer}" }

                                val clipboard = view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Deck Flashcards", formattedText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(view.context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
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
        val binding = ListItemDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DeckDiffCallback : DiffUtil.ItemCallback<Deck>() {
    override fun areItemsTheSame(oldItem: Deck, newItem: Deck): Boolean = oldItem.deckId == newItem.deckId
    override fun areContentsTheSame(oldItem: Deck, newItem: Deck): Boolean = oldItem == newItem
}