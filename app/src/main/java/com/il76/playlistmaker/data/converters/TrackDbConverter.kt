package com.il76.playlistmaker.data.converters

import com.il76.playlistmaker.history.data.db.TrackEntity
import com.il76.playlistmaker.search.domain.models.Track

class TrackDbConverter {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.id,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.poster,
            track.releaseYear
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(track.id,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.poster,
            track.releaseYear)
    }
}