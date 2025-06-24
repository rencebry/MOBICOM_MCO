package com.mobicom.s17.group8.mobicom_mco.music

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemMusicBinding

class MusicAdapter(
    private val musicList: List<MusicTrack>,
    private val onTrackClicked: (MusicTrack?) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var currentlySpinningPosition = -1 // vinyl position

    // manage each music item view
    inner class MusicViewHolder(val binding: ListItemMusicBinding) : RecyclerView.ViewHolder(binding.root) {
        var animator: ObjectAnimator? = null // rotate view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ListItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)
    }

    override fun getItemCount() = musicList.size

    // binding data to each item view
    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val track = musicList[position]

        holder.binding.apply {
            tvMusicTitle.text = "♪ ${track.name}"

            // color in the middle
            val centerColor = ContextCompat.getColor(root.context, track.centerColorResId)
            ivVinylCenter.background.setTint(centerColor)

            holder.animator?.cancel()
            //animate
            if (position == currentlySpinningPosition) {
                holder.animator = ObjectAnimator.ofFloat(ivVinylRecord, "rotation", 0f, 360f).apply {
                    duration = 4000
                    repeatCount = ValueAnimator.INFINITE
                    interpolator = LinearInterpolator()
                }
                holder.animator?.start()
            } else {
                ivVinylRecord.rotation = 0f // if not selected, vinyl stops rotation
            }
        }

        // for clicking items
        holder.binding.root.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            val previousSpinningPosition = currentlySpinningPosition

            // item is clicked again, stop spinning
            if (currentPosition == currentlySpinningPosition) {
                currentlySpinningPosition = -1
                onTrackClicked(null) // clear selection
            } else {
                currentlySpinningPosition = currentPosition
                onTrackClicked(musicList[currentPosition])
            }

            // refresh prev item
            if (previousSpinningPosition != -1) {
                notifyItemChanged(previousSpinningPosition)
            }
            // refresh current item
            notifyItemChanged(currentlySpinningPosition)
        }
    }
}