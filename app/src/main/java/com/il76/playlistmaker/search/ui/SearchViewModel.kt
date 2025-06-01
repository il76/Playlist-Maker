package com.il76.playlistmaker.search.ui

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.il76.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.il76.playlistmaker.search.domain.api.TracksInteractor
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.ui.shared.UIConstants.CLICK_DEBOUNCE_DELAY
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

    private var searchJob: Job? = null

    private val _currentQuery = mutableStateOf("")
    val currentQuery: State<String> get() = _currentQuery

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
        if (changedText.isNullOrEmpty()) {
            toggleHistory(true)
        }
        if (_currentQuery.value == changedText) return
        _currentQuery.value = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(2000L)
            doSearch()
        }
    }

    fun doSearch() {
        if (_currentQuery.value.isEmpty()) return
        _state.value = SearchState(status = SearchState.ErrorStatus.LOADING)

        viewModelScope.launch {
            trackInteractor.searchTracks(_currentQuery.value).collect { foundTracks ->
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
        tracksHistoryInteractor.addTrack(track)
        if (_state.value.status == SearchState.ErrorStatus.HISTORY) {
            _state.value =
                SearchState(
                    status = SearchState.ErrorStatus.HISTORY,
                    trackList = tracksHistoryInteractor.getTracks()
                )
        }
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
        _currentQuery.value = text
        toggleHistory(text.isEmpty())
    }

    fun showToast(message: String) {
        _showToast.postValue(message)
    }

    fun provideTrackData(track: Track): String = Uri.encode(gson.toJson(track))

}