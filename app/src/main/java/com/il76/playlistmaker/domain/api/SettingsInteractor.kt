package com.il76.playlistmaker.domain.api

import com.il76.playlistmaker.domain.models.ThemeSettings

interface SettingsInteractor {

    fun saveThemeSettings(theme: ThemeSettings)
    fun switchTheme(theme: ThemeSettings)
    fun getThemeSettings(): ThemeSettings

}