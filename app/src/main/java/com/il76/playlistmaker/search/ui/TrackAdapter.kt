package com.il76.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.il76.playlistmaker.databinding.SearchTrackListBinding
import com.il76.playlistmaker.search.domain.models.Track

class TrackAdapter(private val tracks: List<Track>, private val clickListener: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(SearchTrackListBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[holder.adapterPosition]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            clickListener(track)
        }
    }

}