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

    private var selectedPosition = -1
    private var isPlaying = false

    inner class MusicViewHolder(val binding: ListItemMusicBinding) : RecyclerView.ViewHolder(binding.root) {
        var animator: ObjectAnimator? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ListItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = MusicViewHolder(binding)

        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            // user clicks the currently selected track (deselect)
            val newTrack = if (currentPosition == selectedPosition) null else musicList[currentPosition]
            onTrackClicked(newTrack)
        }
        return holder
    }

    override fun getItemCount() = musicList.size

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val track = musicList[position]
        holder.binding.apply {
            tvMusicTitle.text = "♪ ${track.name}"
            val centerColor = ContextCompat.getColor(root.context, track.centerColorResId)
            ivVinylCenter.background.setTint(centerColor)

            if (position == selectedPosition && isPlaying) {
                // start rot
                if (holder.animator == null) {
                    holder.animator = ObjectAnimator.ofFloat(ivVinylRecord, "rotation", 0f, 360f).apply {
                        duration = 4000
                        repeatCount = ValueAnimator.INFINITE
                        interpolator = LinearInterpolator()
                    }
                }
                if (!holder.animator!!.isStarted) {
                    holder.animator?.start() // start
                } else if (holder.animator!!.isPaused) {
                    holder.animator?.resume() // resume
                }
            } else {
                // not selected or paused
                holder.animator?.pause() // pause
                if (position != selectedPosition) {
                    ivVinylRecord.rotation = 0f // reset rot
                }
            }
        }
    }

    fun setPlaybackState(newSelectedPosition: Int, newIsPlaying: Boolean) {
        val oldSelectedPosition = selectedPosition
        val oldIsPlaying = isPlaying

        selectedPosition = newSelectedPosition
        isPlaying = newIsPlaying

        // update old item
        if (oldSelectedPosition != -1 && oldSelectedPosition != newSelectedPosition) {
            notifyItemChanged(oldSelectedPosition)
        }
        // update the new item
        if (newSelectedPosition != -1) {
            notifyItemChanged(newSelectedPosition)
        }
    }
}