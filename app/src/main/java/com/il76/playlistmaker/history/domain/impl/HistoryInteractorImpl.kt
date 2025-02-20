package com.il76.playlistmaker.history.domain.impl

import com.il76.playlistmaker.history.domain.db.HistoryInteractor
import com.il76.playlistmaker.history.domain.db.HistoryRepository
import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class HistoryInteractorImpl(
    private val historyRepository: HistoryRepository
) : HistoryInteractor {
    override fun historyTracks(): Flow<List<Track>> {
        return historyRepository.historyTracks()
    }
}