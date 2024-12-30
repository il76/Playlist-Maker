package com.il76.playlistmaker.search.ui

import com.il76.playlistmaker.search.domain.models.Track

data class SearchState(
    val trackList: List<Track> = arrayListOf(),
    val status: ErrorStatus = ErrorStatus.NONE,
    val searchText: String = ""
) {
    enum class ErrorStatus {
        NONE, LOADING, ERROR_NET, EMPTY_RESULT, HISTORY, EMPTY_HISTORY, SUCCESS
    }
}