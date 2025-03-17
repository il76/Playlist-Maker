package com.il76.playlistmaker.media.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.il76.playlistmaker.R
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.databinding.PlayerPlaylistsListBinding

class PlaylistViewHolder(private val binding: PlayerPlaylistsListBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(model: Playlist) {
        Glide.with(binding.root)
            .load(model.cover)
            .placeholder(R.drawable.search_cover_placeholder)
            .centerInside()
            .transform(RoundedCorners(binding.root.context.resources.getDimensionPixelSize(R.dimen.track_cover_border_radius_player)))
            .into(binding.playlistCover)

        binding.playlistName.text = model.name
        binding.playlistCounter.text = model.cnt.toString() + " " + binding.root.context.resources.getString(R.string.playlist_tracks_cnt_description)
    }
}