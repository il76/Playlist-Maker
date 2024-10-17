package com.il76.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track (
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
    val country: String = ""
) {
    fun getPoster(thumb: Boolean = true): String {
        if (thumb) {
            return artworkUrl100
        } else {
            return artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
        }
    }
}