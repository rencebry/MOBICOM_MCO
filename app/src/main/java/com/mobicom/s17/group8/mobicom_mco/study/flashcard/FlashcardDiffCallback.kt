package com.mobicom.s17.group8.mobicom_mco.study.flashcard

import androidx.recyclerview.widget.DiffUtil
import com.mobicom.s17.group8.mobicom_mco.database.study.Flashcard

object FlashcardDiffCallback : DiffUtil.ItemCallback<Flashcard>() {
    override fun areItemsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
        return oldItem.flashcardId == newItem.flashcardId
    }

    override fun areContentsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
        return oldItem == newItem
    }
}