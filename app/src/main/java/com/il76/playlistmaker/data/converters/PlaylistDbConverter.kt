package com.il76.playlistmaker.data.converters

import com.il76.playlistmaker.media.data.db.PlaylistEntity
import com.il76.playlistmaker.media.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            cover = playlist.cover,
            cnt = playlist.cnt
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            cover = playlist.cover,
            cnt = playlist.cnt
        )
    }
}