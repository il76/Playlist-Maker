package com.il76.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.il76.playlistmaker.databinding.SearchTrackListBinding
import com.il76.playlistmaker.domain.models.Track

class TrackAdapter(private val tracks: ArrayList<Track>) : RecyclerView.Adapter<TrackViewHolder> () {

    private lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int, view: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(SearchTrackListBinding.inflate(layoutInspector, parent, false), clickListener)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        //holder.bind(tracks[position])
        holder.bind(tracks[holder.adapterPosition])

    }

    fun onClickListener(listener: OnItemClickListener){
        clickListener = listener
    }
}