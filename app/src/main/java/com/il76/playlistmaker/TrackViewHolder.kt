package com.il76.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View, listener: TrackAdapter.OnItemClickListener): RecyclerView.ViewHolder(itemView) {
    private val cover: ImageView = itemView.findViewById(R.id.track_list_cover)
    private val track: TextView = itemView.findViewById(R.id.track_list_track)
    private val artist: TextView = itemView.findViewById(R.id.track_list_artist)
    private val time: TextView = itemView.findViewById(R.id.track_list_time)
    init {
        itemView.setOnClickListener {
            listener.onItemClick(adapterPosition, itemView)
        }
    }

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.search_cover_placeholder)
            .centerInside()
            .transform(RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.track_cover_border_radius)))
            .into(cover)

        track.text = model.trackName
        artist.text = model.artistName
        artist.requestLayout() // без этой строчки едет вёрстка в строке с исполнителем
        time.text = model.getTime()
    }
}