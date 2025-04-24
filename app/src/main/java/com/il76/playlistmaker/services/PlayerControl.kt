package com.il76.playlistmaker.services

import com.il76.playlistmaker.player.ui.PlayerStatus
import kotlinx.coroutines.flow.StateFlow

interface PlayerControl {
    fun getPlayerStatus(): StateFlow<PlayerStatus>
    fun startPlayer()
    fun pausePlayer()
    fun hideNotification()
    fun showNotification()
}