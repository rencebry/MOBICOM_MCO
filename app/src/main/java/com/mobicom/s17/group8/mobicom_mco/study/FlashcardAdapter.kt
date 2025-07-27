package com.mobicom.s17.group8.mobicom_mco.study

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemFlashcardBinding

class FlashcardAdapter(private val flashcards: List<Flashcard>) : RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {
    inner class FlashcardViewHolder(val binding: ListItemFlashcardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val binding = ListItemFlashcardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlashcardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        val flashcard = flashcards[position]
        holder.binding.flashcardQuestion.text = flashcard.question
        holder.binding.flashcardAnswer.text = flashcard.answer
    }

    override fun getItemCount(): Int = flashcards.size
}
