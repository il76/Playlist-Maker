package com.il76.playlistmaker.media.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistAddViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistId: Int
) : ViewModel() {

    lateinit var playlist: Playlist
    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

    init {
        if (playlistId > 0) {
            viewModelScope.launch {
                playlistInteractor.getSinglePlaylist(playlistId)?.collect { playlistData ->
                    playlist = playlistData
                    playlistLiveData.postValue(playlist)
                }
            }
        }
    }

    private val successLiveData = MutableLiveData<Boolean>()
    fun observeSuccess(): LiveData<Boolean> = successLiveData

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            if (playlist.id > 0) {
                playlistInteractor.updatePlaylist(playlist)
            } else {
                playlistInteractor.createPlaylist(playlist)
            }
            successLiveData.postValue(true)
        }
    }
}