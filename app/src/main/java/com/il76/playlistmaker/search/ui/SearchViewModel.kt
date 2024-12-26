package com.il76.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.il76.playlistmaker.creator.Creator
import com.il76.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.il76.playlistmaker.search.domain.api.TracksInteractor
import com.il76.playlistmaker.utils.SingleLiveEvent

class SearchViewModel(
    trackInteractor: TracksInteractor,
    tracksHistoryInteractor: TracksHistoryInteractor
): ViewModel() {

    private var loadingObserver: ((Boolean) -> Unit)? = null

    fun addLoadingObserver(loadingObserver: ((Boolean) -> Unit)) {
        this.loadingObserver = loadingObserver
    }

    fun removeLoadingObserver() {
        this.loadingObserver = null
    }

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()

    private var searchValueLiveData = MutableLiveData<String>()

    init {
        //playerLiveData.postValue(track)
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    fun observeState(): LiveData<SearchState> = stateLiveData


    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_TOKEN)
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
    }
}