package com.il76.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.il76.playlistmaker.creator.Creator
import com.il76.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.il76.playlistmaker.search.domain.api.TracksInteractor
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.utils.SingleLiveEvent

class SearchViewModel(
    val trackInteractor: TracksInteractor,
    val tracksHistoryInteractor: TracksHistoryInteractor
): ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()

    init {
        stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.NONE))
    }

    private var latestSearchText: String? = null

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    fun observeState(): LiveData<SearchState> = stateLiveData


    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_TOKEN)

        val searchRunnable = Runnable { doSearch(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_TOKEN,
            postTime,
        )
    }


    fun doSearch(text: String) {
        Log.i("dosearch",text)
        if (text.isEmpty()) {
            return
        }
        handler.removeCallbacksAndMessages(SEARCH_TOKEN)
        stateLiveData.postValue(SearchState(status = SearchState.ErrorStatus.LOADING))
        Log.i("pls","searching"+ text)

        trackInteractor.searchTracks(text,
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    handler.post {
                        Log.i("pls", foundTracks.toString())
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
        )
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


    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    Creator.provideTracksInteractor(),
                    Creator.provideTracksHistoryInteractor()
                )
            }
        }
        private val SEARCH_TOKEN = Any()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}