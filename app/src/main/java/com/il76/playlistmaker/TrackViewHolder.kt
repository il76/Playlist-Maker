package com.il76.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val cover: ImageView = itemView.findViewById(R.id.track_list_cover)
    private val track: TextView = itemView.findViewById(R.id.track_list_track)
    private val artist: TextView = itemView.findViewById(R.id.track_list_artist)
    private val time: TextView = itemView.findViewById(R.id.track_list_time)

    fun bind(model: Track) {
        Glide.with(itemView).load(model.artworkUrl100).into(cover)

        track.text = model.trackName
        artist.text = model.artistName
        time.text = model.trackTime
    }
}