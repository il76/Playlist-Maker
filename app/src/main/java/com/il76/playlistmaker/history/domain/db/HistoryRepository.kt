package com.il76.playlistmaker.history.domain.db

import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun historyTracks(): Flow<List<Track>>

}