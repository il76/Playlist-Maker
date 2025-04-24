package com.il76.playlistmaker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.google.gson.Gson
import com.il76.playlistmaker.R
import com.il76.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.player.ui.PlayerStatus
import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PlayerService(): Service() {

    private val gson: Gson by inject()
    private val playerInteractor: MediaPlayerInteractor by inject()

    private val binder = PlayerServiceBinder()

    private lateinit var track: Track

    private val _playerStatus = MutableStateFlow<PlayerStatus>(PlayerStatus.Default)
    val playerStatus = _playerStatus.asStateFlow()

    override fun onBind(intent: Intent?): IBinder? {
        Log.i("pls", "onbind")
        val trackData =  intent?.getStringExtra("track_data") ?: ""
        track = gson.fromJson(trackData, Track::class.java)
        initMediaPlayer()
        _playerStatus.value = PlayerStatus.Loading(track)

        //createNotificationChannel()
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )

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
            _playerStatus.value = PlayerStatus.Prepared
            Log.d("pls", "Media Player prepared")
        }, {
            _playerStatus.value = PlayerStatus.Prepared
            stopTimer()
            Log.d("pls", "Playback completed")
        })

    }
    private var timerJob: Job? = null
    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (_playerStatus.value is PlayerStatus.Playing) {
                _playerStatus.value = PlayerStatus.Playing(playerInteractor.getCurrentTime())
                delay(TIME_REFRESH_INTERVAL)
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
        _playerStatus.value = PlayerStatus.Playing(0)
        startTimer()
    }

    // Приостановка воспроизведения
    fun pausePlayer() {
        Log.i("pls", "pause")
        playerInteractor.pause()
        stopTimer()
        _playerStatus.value = PlayerStatus.Paused
    }

    // Освобождаем все ресурсы, выделенные для плеера
    private fun releasePlayer() {
        Log.i("pls", "release")
        _playerStatus.value = PlayerStatus.Default
        playerInteractor.release()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    private fun createNotificationChannel() {
        // Создание каналов доступно только с Android 8.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Player service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        // Регистрируем канал уведомлений
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("${track.artistName} - ${track.trackName}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }


    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }
    companion object {
        private const val TIME_REFRESH_INTERVAL = 300L
        const val NOTIFICATION_CHANNEL_ID = "player_service_channel"
        const val SERVICE_NOTIFICATION_ID = 100
    }
}