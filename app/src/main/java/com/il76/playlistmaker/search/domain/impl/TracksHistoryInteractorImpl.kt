package com.il76.playlistmaker.search.domain.impl

import com.il76.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.il76.playlistmaker.search.domain.api.TracksHistoryRepository
import com.il76.playlistmaker.search.domain.models.Track

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) :
    TracksHistoryInteractor {

    override fun getTracks(): List<Track> {
        return repository.getTracks()
    }

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override fun saveHistory() {
        repository.saveHistory()
    }
}