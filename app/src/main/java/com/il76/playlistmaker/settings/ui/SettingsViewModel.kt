package com.il76.playlistmaker.settings.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.il76.playlistmaker.creator.Creator
import com.il76.playlistmaker.settings.domain.api.SettingsInteractor
import com.il76.playlistmaker.settings.domain.models.ThemeSettings
import com.il76.playlistmaker.sharing.api.SharingInteractor
import com.il76.playlistmaker.utils.SingleLiveEvent

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
): ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SettingsState>()

    init {
        stateLiveData.postValue(SettingsState(isLoading = false, isChecked =settingsInteractor.getThemeSettings().isDark))
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    fun observeState(): LiveData<SettingsState> = stateLiveData

    private fun renderIsDark(isDark: Boolean) {
        stateLiveData.postValue(SettingsState(false, isDark))
    }

    fun shareApp() {
        val result = sharingInteractor.share()
        if (result.isNotEmpty()) {
            showToast.postValue(result)
        }
    }

    fun openTOS() {
        val result = sharingInteractor.openTOS()
        if (result.isNotEmpty()) {
            showToast.postValue(result)
        }
    }

    fun writeSupport() {
        val result = sharingInteractor.writeSupport()
        if (result.isNotEmpty()) {
            showToast.postValue(result)
        }
    }

    fun switchTheme(isChecked: Boolean) {
        settingsInteractor.switchTheme(ThemeSettings(isChecked))
        settingsInteractor.saveThemeSettings(ThemeSettings(isChecked))
        renderIsDark(isChecked)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SETTINGS_TOKEN)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    Creator.provideSharingInteractor(),
                    Creator.provideSettingsInteractor()
                )
            }
        }
        private val SETTINGS_TOKEN = Any()
    }

}