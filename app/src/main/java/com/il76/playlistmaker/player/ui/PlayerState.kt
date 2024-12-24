package com.il76.playlistmaker.player.ui

data class PlayerState(val state: State) {
    enum class State {
        STATE_DEFAULT, STATE_PREPARED, STATE_PLAYING, STATE_PAUSED
    }
}