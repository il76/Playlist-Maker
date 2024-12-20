package com.il76.playlistmaker.settings.domain.api

import com.il76.playlistmaker.settings.domain.models.ThemeSettings

interface SettingsInteractor {

    fun saveThemeSettings(theme: ThemeSettings)
    fun switchTheme(theme: ThemeSettings)
    fun getThemeSettings(): ThemeSettings

}