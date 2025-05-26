package com.il76.playlistmaker.media.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistAddViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistId: Int
) : ViewModel() {

    lateinit var playlist: Playlist
    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }


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