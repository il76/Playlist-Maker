package com.il76.playlistmaker

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val cover: ImageView = itemView.findViewById(R.id.track_list_cover)
    private val track: TextView = itemView.findViewById(R.id.track_list_track)
    private val artist: TextView = itemView.findViewById(R.id.track_list_artist)
    private val time: TextView = itemView.findViewById(R.id.track_list_time)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.search_cover_placeholder)
            .centerInside()
            //R.dimen.track_cover_border_radius.toFloat() - не работает?
            // Изображение полностью скругляется вне зависимости от того, что туда писать и с какой единицей измерения
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(cover)

        track.text = model.trackName
        artist.text = "" // без этой строчки едет вёрстка в строке с исполнителем
        artist.text = model.artistName
        // не нашёл готовой встроенной функции
        // сделано для совместимости с форматом списка по умолчанию.
        // Потом можно будет удалить, если удалить значения по умолчанию или привести их к Long
        fun String.isNumericString(): Boolean {
            val v = toIntOrNull()
            return when(v) {
                null -> false
                else -> true
            }
        }
        if (model.trackTime.isNumericString()) {
            time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime.toLong())
        } else {
            time.text = model.trackTime
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}