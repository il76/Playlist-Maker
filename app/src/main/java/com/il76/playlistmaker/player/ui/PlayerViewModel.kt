package com.il76.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.il76.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    trackData: String,
    private val playerInteractor: MediaPlayerInteractor,
    gson: Gson
): ViewModel() {

    var track: Track = Track()

    private var playerStatus = PlayerStatus.DEFAULT

    private val playerLiveData = MutableLiveData<PlayerState>()
    private val playerStatusLiveData = MutableLiveData<PlayerStatus>()
    private val currentTimeLiveData = MutableLiveData<String>()

    private var timerJob: Job? = null

    init {
        track = gson.fromJson(trackData, Track::class.java)
        //первичная загрузка трека
        playerLiveData.postValue(
            PlayerState.Loading(track)
        )
        playerInteractor.init(
            dataSource = track.previewUrl,
            onPreparedListener = {
                playerStatusLiveData.postValue(PlayerStatus.PREPARED)
            },
            onCompletionListener = {
                playerStatusLiveData.postValue(PlayerStatus.PREPARED)
                stopTimer()
            }
        )
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    fun observeState(): LiveData<PlayerState> = playerLiveData

    fun observePlayerStatus(): LiveData<PlayerStatus> = playerStatusLiveData
    fun observeCurrentTime(): LiveData<String> = currentTimeLiveData

    fun changePlayerStatus(status: PlayerStatus) {
        playerStatus = status
        when (status) {
            PlayerStatus.DEFAULT -> {}
            PlayerStatus.PREPARED -> {
                playerStatusLiveData.postValue(PlayerStatus.PREPARED)
            }
            PlayerStatus.PLAYIND -> {
                playerInteractor.start()
                startTimer()
                playerStatusLiveData.postValue(PlayerStatus.PLAYIND)
            }
            PlayerStatus.PAUSED -> {
                playerInteractor.pause()
                playerStatusLiveData.postValue(PlayerStatus.PAUSED)
                stopTimer()
            }
        }

    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerStatus == PlayerStatus.PLAYIND) {
                delay(TIME_REFRESH_INTERVAL)
                currentTimeLiveData.postValue(playerInteractor.getCurrentTime())
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        playerInteractor.release()
    }

    companion object {
        private const val TIME_REFRESH_INTERVAL = 300L
    }

}