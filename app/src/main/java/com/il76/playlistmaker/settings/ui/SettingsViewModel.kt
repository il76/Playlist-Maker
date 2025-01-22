package com.il76.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.il76.playlistmaker.settings.domain.api.SettingsInteractor
import com.il76.playlistmaker.settings.domain.models.ThemeSettings
import com.il76.playlistmaker.sharing.api.SharingInteractor
import com.il76.playlistmaker.sharing.data.EmailData
import com.il76.playlistmaker.utils.SingleLiveEvent

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
): ViewModel() {

    private val stateLiveData = MutableLiveData<SettingsState>()

    init {
        stateLiveData.postValue(SettingsState(isLoading = false, isChecked = settingsInteractor.getThemeSettings().isDark))
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    fun observeState(): LiveData<SettingsState> = stateLiveData

    private fun renderIsDark(isDark: Boolean) {
        stateLiveData.postValue(SettingsState(false, isDark))
    }

    fun shareApp(url: String) {
        val result = sharingInteractor.share(url)
        if (result.isNotEmpty()) {
            showToast.postValue(result)
        }
    }

    fun openTOS(url: String) {
        val result = sharingInteractor.openTOS(url)
        if (result.isNotEmpty()) {
            showToast.postValue(result)
        }
    }

    fun writeSupport(emailData: EmailData) {
        val result = sharingInteractor.writeSupport(
            emailData
        )
        if (result.isNotEmpty()) {
            showToast.postValue(result)
        }
    }

    fun switchTheme(isChecked: Boolean) {
        renderIsDark(isChecked)
        settingsInteractor.saveThemeSettings(ThemeSettings(isChecked))
        settingsInteractor.switchTheme(ThemeSettings(isChecked))

    }
}