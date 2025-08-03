package com.mobicom.s17.group8.mobicom_mco.study

import androidx.recyclerview.widget.DiffUtil

object FlashcardDiffCallback : DiffUtil.ItemCallback<Flashcard>() {
    override fun areItemsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
        return oldItem.flashcardId == newItem.flashcardId
    }

    override fun areContentsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
        return oldItem == newItem
    }
}