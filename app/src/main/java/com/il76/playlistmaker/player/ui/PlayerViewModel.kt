package com.il76.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.il76.playlistmaker.creator.Creator
import com.il76.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.utils.SingleLiveEvent

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: MediaPlayerInteractor,
): ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private var playerStatus = PlayerStatus.DEFAULT

    private val playerLiveData = MutableLiveData<PlayerState>()
    private val playerStatusLiveData = MutableLiveData<PlayerStatus>()
    private val currentTimeLiveData = MutableLiveData<String>()

    init {
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
                handler.removeCallbacksAndMessages(null)
            }
        )
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    fun observeState(): LiveData<PlayerState> = playerLiveData

    fun observePlayerStatus(): LiveData<PlayerStatus> = playerStatusLiveData
    fun observeCurrentTime(): LiveData<String> = currentTimeLiveData

    fun changePlayerStatus(status: PlayerStatus) {
        when (status) {
            PlayerStatus.DEFAULT -> {}
            PlayerStatus.PREPARED -> {
                playerStatusLiveData.postValue(PlayerStatus.PREPARED)
            }
            PlayerStatus.PLAYIND -> {
                playerInteractor.start()
                playerStatusLiveData.postValue(PlayerStatus.PLAYIND)
                handler.post(prepareCurrentTimeTask())
            }
            PlayerStatus.PAUSED -> {
                playerInteractor.pause()
                playerStatusLiveData.postValue(PlayerStatus.PAUSED)
                handler.removeCallbacksAndMessages(null)
            }
        }
        playerStatus = status
    }


    private fun prepareCurrentTimeTask(): Runnable {
        return object : Runnable {
            override fun run() {
                // Обновляем список в главном потоке
                displayCurrentPosition()

                // И снова планируем то же действие через TIME_REFRESH_INTERVAL миллисекунд
                handler.postDelayed(
                    this,
                    TIME_REFRESH_INTERVAL,
                )
            }
        }
    }

    /**
     * Обновляем текущее время
     */
    private fun displayCurrentPosition() {
        currentTimeLiveData.postValue(playerInteractor.getCurrentTime())
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(PLAYER_TOKEN)
        playerInteractor.release()
    }

    companion object {
        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    track,
                    Creator.provideMediaPlayerInteractor()
                )
            }
        }
        private val PLAYER_TOKEN = Any()
        private const val TIME_REFRESH_INTERVAL = 500L
    }

}