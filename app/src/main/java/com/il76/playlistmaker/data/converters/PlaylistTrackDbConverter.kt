package com.il76.playlistmaker.data.converters

import com.il76.playlistmaker.media.data.db.PlaylistEntity
import com.il76.playlistmaker.media.data.db.PlaylistTrackEntity
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.search.domain.models.Track

class PlaylistTrackDbConverter {
    fun map(playlistTrack: PlaylistTrack): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            playlistId = playlistTrack.playlist.id,
            trackId = playlistTrack.track.trackId
        )
    }

    fun map(playlistTrack: PlaylistTrackEntity): PlaylistTrack {
        return PlaylistTrack(
            Playlist(playlistTrack.playlistId),
            Track(playlistTrack.trackId),
        )
    }
}