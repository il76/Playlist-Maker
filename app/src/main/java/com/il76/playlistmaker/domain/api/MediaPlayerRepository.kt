package com.il76.playlistmaker.domain.api

interface MediaPlayerRepository {
    fun init(
        dataSource: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    )
    fun start()
    fun stop()
    fun pause()
    fun release()
    fun getCurrentTime(): Int
}