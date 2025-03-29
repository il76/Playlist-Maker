package com.il76.playlistmaker.playlist.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistId: Int, playlistInteractor: PlaylistInteractor): ViewModel() {
    var playlist = Playlist()
    init {
        viewModelScope.launch {
            playlistInteractor.getSinglePlaylist(playlistId)?.collect { playlistData ->
                playlist = playlistData
                Log.i("pls", playlist.toString())
            }
        }
    }
}