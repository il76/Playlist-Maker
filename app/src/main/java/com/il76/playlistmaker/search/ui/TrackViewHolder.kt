package com.il76.playlistmaker.search.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.SearchTrackListBinding
import com.il76.playlistmaker.search.domain.models.Track

class TrackViewHolder(private val binding: SearchTrackListBinding): RecyclerView.ViewHolder(binding.root) {
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