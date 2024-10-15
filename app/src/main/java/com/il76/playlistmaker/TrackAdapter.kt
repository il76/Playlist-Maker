package com.il76.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val tracks: ArrayList<Track>) : RecyclerView.Adapter<TrackViewHolder> () {

    private lateinit var clickListener:OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int, view: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_track_list, parent, false)
        return TrackViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        //holder.bind(tracks[position])
        holder.bind(tracks[holder.adapterPosition])

    }

    fun onClickListener(listener:OnItemClickListener){
        clickListener = listener
    }
}