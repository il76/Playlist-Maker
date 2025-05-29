package com.il76.playlistmaker.search.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.il76.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.il76.playlistmaker.search.domain.api.TracksInteractor
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    val trackInteractor: TracksInteractor,
    val tracksHistoryInteractor: TracksHistoryInteractor,
    private val gson: Gson
) : ViewModel() {

    private val _showToast = SingleLiveEvent<String>()

    val showToast: LiveData<String> get() = _showToast


    private val _state = MutableStateFlow<SearchState>(SearchState())
    val state: StateFlow<SearchState> = _state

    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    private val _trackClicks = MutableSharedFlow<Track>()
    val trackClicks = _trackClicks.asSharedFlow()

    init {
        _state.value = SearchState(status = SearchState.ErrorStatus.NONE)
        viewModelScope.launch {
            _trackClicks
                .debounce(CLICK_DEBOUNCE_DELAY)
                .collect { track ->
                    addToHistory(track)
                }
        }
    }

    fun onTrackClicked(track: Track) {
        viewModelScope.launch {
            _trackClicks.emit(track)
        }
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) return
        latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(2000L)
            doSearch(changedText)
        }
    }

    fun doSearch(text: String) {
        if (text.isEmpty()) return
        _state.value = SearchState(status = SearchState.ErrorStatus.NONE)

        viewModelScope.launch {
            trackInteractor.searchTracks(text).collect { foundTracks ->
                when {
                    foundTracks == null -> {
                        _state.value = SearchState(status = SearchState.ErrorStatus.ERROR_NET)

                    }
                    foundTracks.isNotEmpty() -> {
                        _state.value =
                            SearchState(
                                status = SearchState.ErrorStatus.SUCCESS,
                                trackList = foundTracks
                            )
                    }
                    else -> {
                        _state.value =
                            SearchState(status = SearchState.ErrorStatus.EMPTY_RESULT)
                    }
                }
            }
        }
    }

    fun clearHistory() {
        tracksHistoryInteractor.clearHistory()
        _state.value = SearchState(status = SearchState.ErrorStatus.EMPTY_HISTORY)
    }

    fun addToHistory(track: Track) {
        Log.d("pls", track.toString())
        tracksHistoryInteractor.addTrack(track)
        _state.value =
            SearchState(
                status = SearchState.ErrorStatus.HISTORY,
                trackList = tracksHistoryInteractor.getTracks()
            )
    }

    fun toggleHistory(show: Boolean) {
        if (tracksHistoryInteractor.getTracks().isEmpty() || !show) {
            _state.value = SearchState(status = SearchState.ErrorStatus.EMPTY_HISTORY)
        } else {
            _state.value =
                SearchState(
                    status = SearchState.ErrorStatus.HISTORY,
                    trackList = tracksHistoryInteractor.getTracks()
                )
        }
    }

    fun setSearchText(text: String) {
        latestSearchText = text
        toggleHistory(text.isEmpty())
    }

    fun showToast(message: String) {
        _showToast.postValue(message)
    }

    fun provideTrackData(track: Track): String = Uri.encode(gson.toJson(track))

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"

        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}