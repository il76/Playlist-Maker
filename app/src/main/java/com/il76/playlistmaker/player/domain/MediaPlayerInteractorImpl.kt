package com.il76.playlistmaker.player.domain

import com.il76.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.player.domain.api.MediaPlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerInteractorImpl(private val playerRepository: MediaPlayerRepository):
    MediaPlayerInteractor {
    override fun init(
        dataSource: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        playerRepository.init(dataSource, onPreparedListener, onCompletionListener)
    }

    override fun start() {
        playerRepository.start()
    }

    override fun stop() {
        playerRepository.stop()
    }

    override fun pause() {
        playerRepository.pause()
    }

    override fun release() {
        playerRepository.release()
    }

    override fun getCurrentTime(): Int {
        return playerRepository.getCurrentTime()
    }

}