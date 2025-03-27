package com.il76.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "playlists_tracks",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class PlaylistTrackEntity(
    val playlistId: Int,
    val trackId: Int,
)