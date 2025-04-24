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
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.services.PlayerControl
import com.il76.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class PlayerViewModel(
    val trackData: String,
    gson: Gson,
    private val historyInteractor: HistoryInteractor,
    private val playlistsInteractor: PlaylistInteractor
): ViewModel() {

    var track: Track = Track()

    private val playerStatusLiveData = MutableLiveData<PlayerStatus>(PlayerStatus.Default)
    fun observePlayerStatus(): LiveData<PlayerStatus> = playerStatusLiveData

    private val favouriteLiveData = MutableLiveData<Boolean>()

    private var playerControl: PlayerControl? = null

    init {
        track = gson.fromJson(trackData, Track::class.java)
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    fun observeFavourite(): LiveData<Boolean> = favouriteLiveData

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


    fun setPlayerControl(playerControl: PlayerControl) {
        this.playerControl = playerControl

        viewModelScope.launch {
            playerControl.getPlayerStatus().collect {
                playerStatusLiveData.postValue(it)
            }
        }
    }

    fun playbackControl() {
        if (playerStatusLiveData.value is PlayerStatus.Playing) {
            playerControl?.pausePlayer()
        } else {
            playerControl?.startPlayer()
        }
    }

    fun hideNotification() {
        playerControl?.hideNotification()
    }

    fun showNotification() {
        playerControl?.showNotification()
    }

    fun removePlayerControl() {
        playerControl = null
    }

    override fun onCleared() {
        super.onCleared()
        removePlayerControl()
    }

}