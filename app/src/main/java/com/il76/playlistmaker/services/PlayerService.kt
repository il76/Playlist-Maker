package com.il76.playlistmaker.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.il76.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.player.ui.PlayerStatus
import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PlayerService(): Service() {

    private val gson: Gson by inject()
    private val playerInteractor: MediaPlayerInteractor by inject()

    private val binder = PlayerServiceBinder()

    private lateinit var track: Track

    private var playerStatus = PlayerStatus.DEFAULT

    override fun onBind(intent: Intent?): IBinder? {
        Log.i("pls", "onbind")
        val trackData =  intent?.getStringExtra("track_data") ?: ""
        track = gson.fromJson(trackData, Track::class.java)
        initMediaPlayer()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i("pls", "unbind")
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("pls", "startcommand")
        return Service.START_NOT_STICKY
    }


    // Методы управления Media Player

    // Первичная инициализация плеера
    private fun initMediaPlayer() {
        Log.i("pls", "initmp")
        if (track.previewUrl.isEmpty()) return
        playerInteractor.init(track.previewUrl,{
            playerStatus = PlayerStatus.PREPARED
            Log.d("pls", "Media Player prepared")
        }, {
            playerStatus = PlayerStatus.PREPARED
            stopTimer()
            Log.d("pls", "Playback completed")
        })

    }
    private var timerJob: Job? = null
    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (playerStatus == PlayerStatus.PLAYING) {
                delay(TIME_REFRESH_INTERVAL)
                //currentTimeLiveData.postValue(playerInteractor.getCurrentTime())
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }


    // Запуск воспроизведения
    fun startPlayer() {
        Log.i("pls", "start")
        playerInteractor.start()
        playerStatus == PlayerStatus.PLAYING
    }

    // Приостановка воспроизведения
    fun pausePlayer() {
        Log.i("pls", "pause")
        playerInteractor.pause()
        stopTimer()
        playerStatus == PlayerStatus.PAUSED
    }

    // Освобождаем все ресурсы, выделенные для плеера
    private fun releasePlayer() {
        Log.i("pls", "release")
        playerInteractor.release()
    }


    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }
    companion object {
        private const val TIME_REFRESH_INTERVAL = 300L
    }
}