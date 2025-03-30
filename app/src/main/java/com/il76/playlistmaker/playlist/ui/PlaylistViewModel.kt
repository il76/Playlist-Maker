package com.il76.playlistmaker.playlist.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistId: Int, playlistInteractor: PlaylistInteractor): ViewModel() {

    var playlist: Playlist? = null

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

    init {
        viewModelScope.launch {
            playlistInteractor.getSinglePlaylist(playlistId)?.collect { playlistData ->
                playlist = playlistData
                playlistLiveData.postValue(playlist)
                Log.i("pls", playlist.toString())
            }
        }
    }
}