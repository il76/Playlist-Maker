package com.il76.playlistmaker.history.presentation

import com.il76.playlistmaker.search.domain.models.Track

sealed interface HistoryState {

    object Loading : HistoryState

    data class Content(
        val movies: List<Track>
    ) : HistoryState

    data class Empty(
        val message: String
    ) : HistoryState
}