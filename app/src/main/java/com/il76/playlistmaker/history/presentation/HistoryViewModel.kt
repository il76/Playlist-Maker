package com.il76.playlistmaker.history.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.il76.playlistmaker.R
import com.il76.playlistmaker.history.domain.db.HistoryInteractor
import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val applicationContext: Context,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<HistoryState>()

    fun observeState(): LiveData<HistoryState> = stateLiveData

    fun fillData() {
        renderState(HistoryState.Loading)
        viewModelScope.launch {
            historyInteractor
                .historyTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(movies: List<Track>) {
        if (movies.isEmpty()) {
            renderState(HistoryState.Empty(applicationContext.getString(R.string.search_nothing_found)))
        } else {
            renderState(HistoryState.Content(movies))
        }
    }

    private fun renderState(state: HistoryState) {
        stateLiveData.postValue(state)
    }
}