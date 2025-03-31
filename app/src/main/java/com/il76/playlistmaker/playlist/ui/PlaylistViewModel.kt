package com.il76.playlistmaker.playlist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.search.domain.api.TracksInteractor
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.sharing.api.SharingInteractor
import com.il76.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor,
    private val tracksInteractor:
    TracksInteractor,
    private val gson: Gson,
    private val sharingInteractor: SharingInteractor
    ): ViewModel() {

    var playlist: Playlist? = null
    var tracksList: List<Track>? = null

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData
    private val tracksLiveData = MutableLiveData<List<Track>>()
    fun observeTrackslist(): LiveData<List<Track>> = tracksLiveData
    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    init {
        viewModelScope.launch {
            playlistInteractor.getSinglePlaylist(playlistId)?.collect { playlistData ->
                playlist = playlistData
                playlistLiveData.postValue(playlist)
            }
            if (playlist != null) {
                loadTracks()
            }
        }
    }

    fun loadPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getSinglePlaylist(playlistId)?.collect { playlistData ->
                playlist = playlistData
                playlistLiveData.postValue(playlist)
            }
        }
    }

    fun loadTracks() {
        viewModelScope.launch {
            tracksInteractor.getPlaylistTracks(playlist!!.id).collect { tracksData ->
                tracksList = tracksData
                tracksLiveData.postValue(tracksList)
            }
        }
    }

    fun provideTrackData(track: Track): String {
        return gson.toJson(track)
    }

    fun sharePlaylist() {
        var share_text = "В этом плейлисте нет списка треков, которым можно поделиться"
        if (!tracksList.isNullOrEmpty() && playlist != null) {
            share_text = playlist?.name + "\n" + playlist?.description + "\n" + tracksList?.size + " треков" + "\n\n"
            var i = 1
            for (track in tracksList!!) {
                share_text += i.toString() + ". " + track.artistName + " - " + track.trackName + " (" + track.trackTime + ")" + "\n"
                i++
            }
            sharingInteractor.share(share_text)
        } else {
            showToast.postValue(share_text)
        }
    }

    fun deletePlaylist(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            if (playlist != null) {
                playlistInteractor.deletePlaylist(playlist!!)
            }
            result.postValue(true)
        }
        return result
    }

    fun deleteTrackFromPlaylist(playlistTrack: PlaylistTrack): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistTrack)
            result.postValue(true)
        }
        return result
    }
}