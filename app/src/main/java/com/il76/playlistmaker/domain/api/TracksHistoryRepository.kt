package com.il76.playlistmaker.domain.api

import com.il76.playlistmaker.domain.models.Track

interface TracksHistoryRepository {
    fun getTracks(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
    fun saveHistory()
}