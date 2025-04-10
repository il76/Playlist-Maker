package com.il76.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.il76.playlistmaker.data.db.InsertStatus
import com.il76.playlistmaker.history.domain.db.HistoryInteractor
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    trackData: String,
    private val playerInteractor: MediaPlayerInteractor,
    gson: Gson,
    private val historyInteractor: HistoryInteractor,
    private val playlistsInteractor: PlaylistInteractor
): ViewModel() {

    var track: Track = Track()

    var playerStatus = PlayerStatus.DEFAULT

    private val playerLiveData = MutableLiveData<PlayerState>()
    private val playerStatusLiveData = MutableLiveData<PlayerStatus>()
    private val currentTimeLiveData = MutableLiveData<String>()
    private val favouriteLiveData = MutableLiveData<Boolean>()

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
    fun observeFavourite(): LiveData<Boolean> = favouriteLiveData

    fun changePlayerStatus(status: PlayerStatus) {
        playerStatus = status
        when (status) {
            PlayerStatus.DEFAULT -> {}
            PlayerStatus.PREPARED -> {
                playerStatusLiveData.postValue(PlayerStatus.PREPARED)
            }
            PlayerStatus.PLAYING -> {
                playerInteractor.start()
                startTimer()
                playerStatusLiveData.postValue(PlayerStatus.PLAYING)
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
            while (playerStatus == PlayerStatus.PLAYING) {
                delay(TIME_REFRESH_INTERVAL)
                currentTimeLiveData.postValue(playerInteractor.getCurrentTime())
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    suspend fun toggleFavouriteStatus() {
        track.isFavourite = !track.isFavourite
        if (track.isFavourite) {
            historyInteractor.addTrack(track)
        } else {
            historyInteractor.delTrack(track)
        }
        favouriteLiveData.postValue(track.isFavourite)
    }
    private val playlistsLiveData = MutableLiveData<List<Playlist>?>()
    fun observePlaylistsList(): MutableLiveData<List<Playlist>?> = playlistsLiveData

    private val addTrackResultLiveData = MutableLiveData<InsertStatus>()
    fun observeaddTrackResult(): MutableLiveData<InsertStatus> = addTrackResultLiveData

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                playlistsLiveData.postValue(playlists)
            }
        }
    }

    fun addToPlaylist(playlistTrack: PlaylistTrack) {
        viewModelScope.launch {
            val result = playlistsInteractor.addTrackToPlaylist(playlistTrack)
            if (result == InsertStatus.SUCCESS) {
                showToast.postValue("Добавлено в плейлист \"" + playlistTrack.playlist.name + "\"")
                loadPlaylists()
            } else {
                showToast.postValue("Трек уже добавлен в плейлист \"" + playlistTrack.playlist.name + "\"")
            }
        }
    }

    override fun onCleared() {
        playerInteractor.release()
    }

    companion object {
        private const val TIME_REFRESH_INTERVAL = 300L
    }

}