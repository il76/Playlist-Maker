package com.il76.playlistmaker.settings.ui

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

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
): ViewModel() {

    private val isDarkLiveData = MutableLiveData<SettingsState>()

    init {
        isDarkLiveData.postValue(SettingsState(isLoading = false, isChecked =settingsInteractor.getThemeSettings().isDark))
    }


    fun observeState(): LiveData<SettingsState> = isDarkLiveData

    private fun renderIsDark(isDark: Boolean) {
        isDarkLiveData.postValue(SettingsState(false, isDark))
    }

    fun shareApp() {
        sharingInteractor.share()
    }

    fun openTOS() {
        sharingInteractor.openTOS()
    }

    fun writeSupport() {
        sharingInteractor.writeSupport()
    }

    fun switchTheme(isChecked: Boolean) {
        settingsInteractor.switchTheme(ThemeSettings(isChecked))
        settingsInteractor.saveThemeSettings(ThemeSettings(isChecked))
        renderIsDark(isChecked)
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
    }

}