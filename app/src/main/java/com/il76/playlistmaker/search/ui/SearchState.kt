package com.il76.playlistmaker.search.ui

data class SearchState(
    val isLoading: Boolean,
    val status: ErrorStatus
) {
    enum class ErrorStatus {
        NONE, ERROR_NET, EMPTY_RESULT
    }
}