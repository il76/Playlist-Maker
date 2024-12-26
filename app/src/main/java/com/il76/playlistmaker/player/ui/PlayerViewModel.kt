package com.il76.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.il76.playlistmaker.creator.Creator
import com.il76.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.utils.SingleLiveEvent

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: MediaPlayerInteractor
): ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val playerLiveData = MutableLiveData<Track>()

    init {
        playerLiveData.postValue(track)
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    fun observeState(): LiveData<Track> = playerLiveData


    override fun onCleared() {
        handler.removeCallbacksAndMessages(PLAYER_TOKEN)
    }

    companion object {
        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    track,
                    Creator.provideMediaPlayerInteractor()
                )
            }
        }
        private val PLAYER_TOKEN = Any()
    }

}