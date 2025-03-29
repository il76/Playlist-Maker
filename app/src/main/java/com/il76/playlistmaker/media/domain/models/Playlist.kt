package com.il76.playlistmaker.media.domain.models

data class Playlist(
        val id: Int = 0,
        val name: String = "",
        val description: String = "",
        val cover: String = "",
        val cnt: Int = 0,
)