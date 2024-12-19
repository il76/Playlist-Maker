package com.il76.playlistmaker.domain.api

import com.il76.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(term: String): List<Track>?
}