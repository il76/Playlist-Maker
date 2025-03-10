package com.il76.playlistmaker.history.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val cover: String,
)