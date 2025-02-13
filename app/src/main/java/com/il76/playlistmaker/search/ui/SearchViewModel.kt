package com.il76.playlistmaker.search.ui

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
import kotlinx.coroutines.launch

class SearchViewModel(
    val trackInteractor: TracksInteractor,
    val tracksHistoryInteractor: TracksHistoryInteractor,
    private val gson: Gson
): ViewModel() {


    private val stateLiveData = MutableLiveData<SearchState>()

    init {
        stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.NONE))
    }

    private var latestSearchText: String? = null

    private var searchJob: Job? = null

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    fun observeState(): LiveData<SearchState> = stateLiveData

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            doSearch(changedText)
        }
    }


    fun doSearch(text: String) {
        if (text.isEmpty()) {
            return
        }
        stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.LOADING))

        viewModelScope.launch {
            trackInteractor.searchTracks(text).collect { foundTracks ->
                if (foundTracks == null) {
                    stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.ERROR_NET))
                } else if (foundTracks.isNotEmpty()) {
                    stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.SUCCESS, trackList = foundTracks))
                } else {
                    stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.EMPTY_RESULT))
                }
            }
        }
    }


    fun clearHistory() {
        tracksHistoryInteractor.clearHistory()
        stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.EMPTY_HISTORY))
    }
    fun addToHistory(track: Track) {
        tracksHistoryInteractor.addTrack(track)
        stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.HISTORY, trackList = tracksHistoryInteractor.getTracks()))
    }
    fun toggleHistory(show: Boolean) {
        if (tracksHistoryInteractor.getTracks().isEmpty() or !show) {
            stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.EMPTY_HISTORY))
        } else {
            stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.HISTORY, trackList = tracksHistoryInteractor.getTracks()))
        }
    }

    fun setSearchText(text: String) {
        latestSearchText = text
        toggleHistory(text.isEmpty())
    }

    fun showToast(text: String) {
        showToast.postValue(text)
    }

    fun provideTrackData(track: Track): String {
        return gson.toJson(track)
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}