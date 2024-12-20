package com.il76.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import com.il76.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsViewModel(
    // private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
): ViewModel() {
    private var loadingObserver: ((Boolean) -> Unit)? = null

    var isLoading: Boolean = true
        private set(value) {
            field = value
            loadingObserver?.invoke(value)
        }

    fun addLoadingObserver(loadingObserver: ((Boolean) -> Unit)) {
        this.loadingObserver = loadingObserver
    }

    fun removeLoadingObserver() {
        this.loadingObserver = null
    }
}