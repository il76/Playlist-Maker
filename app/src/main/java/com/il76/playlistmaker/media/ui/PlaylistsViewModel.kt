package com.il76.playlistmaker.media.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistInteractor) : ViewModel() {

    private val playlistsLiveData = MutableLiveData<List<Playlist>?>()
    fun observePlaylistsList(): MutableLiveData<List<Playlist>?> = playlistsLiveData

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                playlistsLiveData.postValue(playlists)
            }
        }
    }
}