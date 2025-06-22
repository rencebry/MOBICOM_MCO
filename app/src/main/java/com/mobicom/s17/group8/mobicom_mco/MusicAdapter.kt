package com.mobicom.s17.group8.mobicom_mco

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemMusicBinding

class MusicAdapter(
    private val musicList: List<MusicTrack>,
    private val onTrackClicked: (MusicTrack) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    inner class MusicViewHolder(val binding: ListItemMusicBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ListItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)
    }

    override fun getItemCount() = musicList.size

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val track = musicList[position]
        holder.binding.apply {
            // trackname
            tvMusicTitle.text = "♪ ${track.name}"

            // color in the middle
            val centerColor = ContextCompat.getColor(root.context, track.centerColorResId)
            ivVinylCenter.background.setTint(centerColor)

            root.setOnClickListener {
                onTrackClicked(track)
            }
        }
    }
}