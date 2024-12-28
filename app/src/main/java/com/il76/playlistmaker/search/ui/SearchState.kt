package com.il76.playlistmaker.search.ui

import com.il76.playlistmaker.search.domain.models.Track

data class SearchState(
    val isLoading: Boolean,
    val trackList: ArrayList<Track>,
    val status: ErrorStatus
) {
    enum class ErrorStatus {
        NONE, ERROR_NET, EMPTY_RESULT
    }
}