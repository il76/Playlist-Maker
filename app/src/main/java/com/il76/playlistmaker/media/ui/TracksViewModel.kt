package com.il76.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.il76.playlistmaker.history.domain.db.HistoryRepository
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.ui.shared.UIConstants.CLICK_DEBOUNCE_DELAY
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce

import kotlinx.coroutines.launch

class TracksViewModel(private val gson: Gson, private val historyRepository: HistoryRepository): ViewModel() {

    private val tracksLiveData = MutableLiveData<List<Track>>()
    fun observeTracksList(): LiveData<List<Track>> = tracksLiveData


    private val _trackClicks = MutableSharedFlow<Track>()
    val trackClicks = _trackClicks.asSharedFlow()

    fun getTracks() {
        viewModelScope.launch {
            historyRepository.historyTracks()
                .collect { tracks ->
                    tracksLiveData.postValue(tracks)
                }
        }
    }

    fun provideTrackData(track: Track): String {
        return gson.toJson(track.apply { isFavourite = true })
    }
    fun init() {
        viewModelScope.launch {
            _trackClicks
                .debounce(CLICK_DEBOUNCE_DELAY)
                //.collect {}
        }
    }

}