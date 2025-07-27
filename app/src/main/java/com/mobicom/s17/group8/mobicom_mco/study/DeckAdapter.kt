package com.mobicom.s17.group8.mobicom_mco.study

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemDeckBinding
import com.mobicom.s17.group8.mobicom_mco.R

class DeckAdapter(
    private val onPlayClicked: (Deck) -> Unit
) : RecyclerView.Adapter<DeckAdapter.DeckViewHolder>() {

    private var deckList: List<Deck> = emptyList()

    fun submitList(decks: List<Deck>) {
        deckList = decks
        notifyDataSetChanged()
    }

    inner class DeckViewHolder(private val binding: ListItemDeckBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(deck: Deck) {
            binding.taskNameTv.text = deck.name
            val countText = binding.root.context.getString(R.string.deck_stat, deck.dateCreated, deck.cardCount)
            binding.taskInfoTv.text = countText

            binding.deckPlay.setOnClickListener {
                onPlayClicked(deck)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDeckBinding.inflate(inflater, parent, false)
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(deckList[position])
    }

    override fun getItemCount(): Int = deckList.size
}