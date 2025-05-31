package com.il76.playlistmaker.media.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.il76.playlistmaker.history.domain.db.HistoryRepository
import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TracksViewModel(
    private val gson: Gson,
    private val historyRepository: HistoryRepository,
) : ViewModel() {

    // у нас flow, поэтому можно сделать запрос по факту подписки
    val tracks = historyRepository.historyTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Для кликов
    private val _trackEvents = MutableSharedFlow<TrackEvent>()
    val trackEvents = _trackEvents.asSharedFlow()

    fun onTrackClicked(track: Track) {
        viewModelScope.launch {
            _trackEvents.emit(TrackEvent.TrackClicked(track))
        }
    }

    fun provideTrackData(track: Track): String {
        return gson.toJson(track.apply { isFavourite = true })
    }
}

sealed class TrackEvent {
    data class TrackClicked(val track: Track) : TrackEvent()
}