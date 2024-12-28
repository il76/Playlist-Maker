package com.il76.playlistmaker.search.ui

import com.il76.playlistmaker.search.domain.models.Track

data class SearchState(
    val isLoading: Boolean,
    val trackList: ArrayList<Track> = arrayListOf(),
    val status: ErrorStatus = ErrorStatus.NONE,
) {
    enum class ErrorStatus {
        NONE, LOADING, ERROR_NET, EMPTY_RESULT, SUCCESS
    }
}