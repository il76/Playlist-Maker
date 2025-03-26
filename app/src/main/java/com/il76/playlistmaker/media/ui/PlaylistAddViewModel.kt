package com.il76.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistAddViewModel(private val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val successLiveData = MutableLiveData<Boolean>()
    fun observeSuccess(): LiveData<Boolean> = successLiveData

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(playlist)
            successLiveData.postValue(true)
        }
    }
}