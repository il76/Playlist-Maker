package com.il76.playlistmaker.domain.api

import com.il76.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchMovies(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}