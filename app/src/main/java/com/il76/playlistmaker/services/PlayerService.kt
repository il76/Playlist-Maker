package com.il76.playlistmaker.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.il76.playlistmaker.player.domain.api.MediaPlayerRepository
import com.il76.playlistmaker.search.domain.models.Track

class PlayerService(private val gson: Gson, private val playerRepository: MediaPlayerRepository): Service() {

    private val binder = PlayerServiceBinder()

    private lateinit var track: Track

    override fun onBind(intent: Intent?): IBinder? {
        val trackData =  intent?.getStringExtra("track_data") ?: ""
        track = gson.fromJson(trackData, Track::class.java)
        initMediaPlayer()
        return binder
    }
    override fun onDestroy() {
        releasePlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }


    // Методы управления Media Player

    // Первичная инициализация плеера
    private fun initMediaPlayer() {
        if (track.previewUrl.isEmpty()) return
        playerRepository.init(track.previewUrl,{
            Log.d("pls", "Media Player prepared")
        }, {
            Log.d("pls", "Playback completed")
        })

    }

    // Запуск воспроизведения
    fun startPlayer() {
        playerRepository.start()
    }

    // Приостановка воспроизведения
    fun pausePlayer() {
        playerRepository.pause()
    }

    // Освобождаем все ресурсы, выделенные для плеера
    private fun releasePlayer() {
        playerRepository.release()
    }


    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

}