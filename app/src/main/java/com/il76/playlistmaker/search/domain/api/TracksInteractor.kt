package com.il76.playlistmaker.search.domain.api

import com.il76.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}