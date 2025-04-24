package com.il76.playlistmaker.player.ui

import com.il76.playlistmaker.search.domain.models.Track

sealed class PlayerStatus {
    data class Loading(val track: Track) : PlayerStatus()
    data object Default : PlayerStatus()
    data object Prepared : PlayerStatus()
    data class Playing(var progress: Int) : PlayerStatus()
    data object Paused : PlayerStatus()
}
