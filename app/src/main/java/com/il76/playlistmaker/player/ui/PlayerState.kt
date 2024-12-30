package com.il76.playlistmaker.player.ui

import com.il76.playlistmaker.search.domain.models.Track

sealed class PlayerState {
    data class Loading(val track: Track): PlayerState()
}