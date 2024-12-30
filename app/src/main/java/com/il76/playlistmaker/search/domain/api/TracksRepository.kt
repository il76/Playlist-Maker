package com.il76.playlistmaker.search.domain.api

import com.il76.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(term: String): List<Track>?
}