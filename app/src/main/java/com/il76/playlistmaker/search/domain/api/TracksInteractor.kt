package com.il76.playlistmaker.search.domain.api

import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(term: String): Flow<List<Track>?>
}