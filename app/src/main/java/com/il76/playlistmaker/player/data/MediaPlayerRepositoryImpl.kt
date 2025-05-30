package com.il76.playlistmaker.player.data

import android.media.MediaPlayer
import com.il76.playlistmaker.player.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl(private val player: MediaPlayer): MediaPlayerRepository {
    override fun init(
        dataSource: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        player.setDataSource(dataSource)
        player.prepareAsync()
        player.setOnPreparedListener {onPreparedListener()}
        player.setOnCompletionListener {onCompletionListener()}
    }

    override fun start() {
        player.start()
    }

    override fun stop() {
        player.stop()
    }

    override fun pause() {
        if (player.isPlaying) {
            player.pause()
        }
    }

    override fun release() {
        player.release()
    }
    override fun getCurrentTime(): Int {
        return player.currentPosition
    }

}

