package com.il76.playlistmaker.history.domain.db

import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface HistoryInteractor {
    fun historyTracks(): Flow<List<Track>>
}