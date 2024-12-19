package com.il76.playlistmaker.data

import com.il76.playlistmaker.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.domain.api.MediaPlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerInteractorImpl(private val playerRepository: MediaPlayerRepository): MediaPlayerInteractor {
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

    override fun getCurrentTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerRepository.getCurrentTime())
    }

}