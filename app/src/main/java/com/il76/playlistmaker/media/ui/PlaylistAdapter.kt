package com.il76.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.il76.playlistmaker.databinding.PlayerPlaylistsListBinding
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.search.domain.models.Track

class PlaylistAdapter(private val playlists: List<Playlist>, private val track: Track, private val clickListener: (PlaylistTrack) -> Unit) : RecyclerView.Adapter<PlaylistViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(PlayerPlaylistsListBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[holder.adapterPosition]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            clickListener(PlaylistTrack(playlist, track))
        }
    }

}