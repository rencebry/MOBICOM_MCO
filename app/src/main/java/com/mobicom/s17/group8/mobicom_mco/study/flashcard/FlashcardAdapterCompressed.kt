package com.mobicom.s17.group8.mobicom_mco.study.flashcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.R
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemFlashcardCompressedBinding
import androidx.recyclerview.widget.ListAdapter
import com.mobicom.s17.group8.mobicom_mco.database.study.Flashcard

class FlashcardAdapterCompressed(
    private val onDeleteFlashcard: (Flashcard) -> Unit,
    private val onUpdateFlashcard: (Flashcard) -> Unit,
    private var showOnlyFavorites: Boolean = false
) : ListAdapter<Flashcard, FlashcardAdapterCompressed.FlashcardViewHolder>(FlashcardDiffCallback) {
        inner class FlashcardViewHolder(val binding: ListItemFlashcardCompressedBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
            val binding = ListItemFlashcardCompressedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return FlashcardViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
            val flashcard = getItem(position)
            val binding = holder.binding
            binding.flashcardQuestion.text = flashcard.question
            binding.flashcardAnswer.text = flashcard.answer

            // Filter visibility + animation
            val shouldShow = !showOnlyFavorites || flashcard.isFavorite

            binding.root.animate().cancel() // cancel previous animations
            binding.root.animate()
                .alpha(if (shouldShow) 1f else 0f)
                .setDuration(150)
                .withEndAction {
                    binding.root.visibility = if (shouldShow) View.VISIBLE else View.GONE
                }.start()

            // Favorite icon toggle
            binding.favoriteCard.setImageResource(
                if (flashcard.isFavorite) R.drawable.ic_star_enabled else R.drawable.ic_star_disabled
            )
            binding.favoriteCard.setOnClickListener {
                val newFavoriteState = !flashcard.isFavorite
                val updatedFlashcard = flashcard.copy(isFavorite = newFavoriteState)

                onUpdateFlashcard(updatedFlashcard) // Trigger view model update

                // Reflect change in UI
                binding.favoriteCard.setImageResource(
                    if (newFavoriteState) R.drawable.ic_star_enabled else R.drawable.ic_star_disabled
                )
            }

            // Flashcard pop-up options
            binding.flashcardOptions.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.inflate(R.menu.flashcard_menu)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {

                        R.id.delete_flashcard -> {
                            // Play fade animation first
                            val fadeOut = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)

                            holder.itemView.startAnimation(fadeOut)

                            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {
                                    onDeleteFlashcard(flashcard)
                                }

                                override fun onAnimationEnd(animation: Animation?) {
                                }

                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                            true
                        }

                        R.id.copy_text_flashcard -> {
                            val clipboard = view.context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val textToCopy = "Q: ${flashcard.question}\nA: ${flashcard.answer}"
                            val clip = android.content.ClipData.newPlainText("Flashcard", textToCopy)
                            clipboard.setPrimaryClip(clip)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }

    fun setShowOnlyFavorites(value: Boolean, fullList: List<Flashcard>) {
        showOnlyFavorites = value

        val filtered = if (showOnlyFavorites) {
            fullList.filter { it.isFavorite }
        } else {
            fullList
        }

        submitList(filtered)
    }
}