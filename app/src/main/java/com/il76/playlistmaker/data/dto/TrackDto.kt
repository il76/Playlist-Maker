package com.il76.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TrackDto (
    /**
     * Название композиции
     */
    val trackName: String = "",
    /**
     * Имя исполнителя
     */
    val artistName: String = "",
    /**
     * Продолжительность трека
     */
    @SerializedName("trackTimeMillis") var trackTime: String = "",
    /**
     * Ссылка на изображение обложки
     */
    val artworkUrl100: String = "",
    /**
     * Внутренний id трека
     */
    val trackId: Int = 0,
    /**
     * Название альбома
     */
    val collectionName: String = "",
    /**
     * Год релиза трека
     */
    val releaseDate: String = "",
    /**
     * Жанр трека
     */
    val primaryGenreName: String = "",
    /**
     * Страна исполнителя
     */
    val country: String = "",
    /**
     * Аудиопоток
     */
    val previewUrl: String = ""
) {
    fun getPoster(thumb: Boolean = true): String {
        if (thumb) {
            return artworkUrl100
        } else {
            return artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
        }
    }

    /**
     * Форматированная длительность трека
     */
    fun getTime(): String {
        if (trackTime.isNotEmpty()) {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime.toLong())
        } else {
            return ""
        }
    }

    /**
     * Год выпуска из исходной даты в ISO формате
     */
    fun getReleaseYear(): String {
        val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date: Date = isoDateFormat.parse(releaseDate)
        val yearDateFormat = SimpleDateFormat("yyyy")
        return yearDateFormat.format(date)
    }
}