package com.il76.playlistmaker.data

import android.media.MediaPlayer
import com.il76.playlistmaker.domain.api.MediaPlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerInteractorImpl(private val player: MediaPlayer): MediaPlayerInteractor {
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
        player.pause()
    }

    override fun release() {
        player.release()
    }

    override fun getState() {
        TODO("Not yet implemented")
    }

    override fun setState() {
        TODO("Not yet implemented")
    }

    override fun getCurrentTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(player.currentPosition)
    }

}