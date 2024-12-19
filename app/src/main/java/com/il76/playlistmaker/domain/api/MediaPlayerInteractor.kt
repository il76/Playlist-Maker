package com.il76.playlistmaker.domain.api

interface MediaPlayerInteractor {
    fun init(
        dataSource: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    )
    fun start()
    fun stop()
    fun pause()
    fun release()
    fun getCurrentTime(): String
}