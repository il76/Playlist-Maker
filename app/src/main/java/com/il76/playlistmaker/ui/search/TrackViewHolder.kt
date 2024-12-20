package com.il76.playlistmaker.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.SearchTrackListBinding
import com.il76.playlistmaker.domain.models.Track

class TrackViewHolder(private val binding: SearchTrackListBinding, listener: TrackAdapter.OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            listener.onItemClick(adapterPosition, itemView)
        }
    }

    fun bind(model: Track) {
        Glide.with(binding.root)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.search_cover_placeholder)
            .centerInside()
            .transform(RoundedCorners(binding.root.context.resources.getDimensionPixelSize(R.dimen.track_cover_border_radius)))
            .into(binding.trackListCover)

        binding.trackListTrack.text = model.trackName
        binding.trackListArtist.text = model.artistName
        binding.trackListArtist.requestLayout() // без этой строчки едет вёрстка в строке с исполнителем
        binding.trackListTime.text = model.trackTime
    }
}