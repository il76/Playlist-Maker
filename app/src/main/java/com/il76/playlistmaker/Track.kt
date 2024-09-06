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
    @SerializedName("trackTimeMillis") val trackTime: String = "",
    /**
     * Ссылка на изображение обложки
     */
    val artworkUrl100: String = ""
)