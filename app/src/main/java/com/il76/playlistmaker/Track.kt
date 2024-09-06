package com.il76.playlistmaker

data class Track (
    /**
     * Название композиции
     */
    var trackName: String = "",
    /**
     * Имя исполнителя
     */
    var artistName: String = "",
    /**
     * Продолжительность трека
     */
    var trackTime: String = "",
    /**
     * Ссылка на изображение обложки
     */
    var artworkUrl100: String = ""
)