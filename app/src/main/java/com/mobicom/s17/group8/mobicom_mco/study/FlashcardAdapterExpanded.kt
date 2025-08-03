package com.mobicom.s17.group8.mobicom_mco.study

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemFlashcardExpandedBinding
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.core.view.isGone
import com.mobicom.s17.group8.mobicom_mco.R
import androidx.recyclerview.widget.ListAdapter

class FlashcardAdapterExpanded(
    private val onDeleteFlashcard: (Flashcard) -> Unit,
    private val onEditFlashcard: (Flashcard) -> Unit,
    private val onUpdateFlashcard: (Flashcard) -> Unit,
    private var showOnlyFavorites: Boolean = false
) : ListAdapter<Flashcard, FlashcardAdapterExpanded.FlashcardViewHolder>(FlashcardDiffCallback) {
        inner class FlashcardViewHolder(val binding: ListItemFlashcardExpandedBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun resetToQuestionSide() {
                    binding.front.visibility = View.VISIBLE
                    binding.back.visibility = View.GONE
                }
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
            val binding = ListItemFlashcardExpandedBinding.inflate(
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

            // Ensure question side is visible
            holder.resetToQuestionSide()

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
                        R.id.edit_flashcard -> {
                            EditFlashcardDialogFragment(flashcard, onEditFlashcard)
                                .show((view.context as androidx.fragment.app.FragmentActivity).supportFragmentManager, "edit_flashcard")
                            true
                        }

                        R.id.delete_flashcard -> {
                            // Play fade animation first
                            val fadeOut = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)

                            holder.itemView.startAnimation(fadeOut)

                            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {
                                    onDeleteFlashcard(flashcard)
                                }

                                override fun onAnimationEnd(animation: Animation?) {}
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

            // Flipping
            binding.front.visibility = View.VISIBLE
            binding.back.visibility = View.GONE
            binding.root.setOnClickListener {
                if (binding.back.isGone) {
                    flipCard(binding.front, binding.back)
                } else {
                    flipCard(binding.back, binding.front)
                }
            }
        }

        // Flipping animation
        private fun flipCard(fromView: View, toView: View) {
            val scale = fromView.context.resources.displayMetrics.density
            fromView.cameraDistance = 8000 * scale
            toView.cameraDistance = 8000 * scale

            val flipOut = ObjectAnimator.ofFloat(fromView, "rotationY", 0f, 90f)
            val flipIn = ObjectAnimator.ofFloat(toView, "rotationY", -90f, 0f)

            flipOut.duration = 150
            flipIn.duration = 150

            flipOut.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fromView.visibility = View.GONE
                    toView.visibility = View.VISIBLE
                    flipIn.start()
                }
            })

            flipOut.start()
        }

        fun resetAllCardsToFront() {
            notifyDataSetChanged()
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