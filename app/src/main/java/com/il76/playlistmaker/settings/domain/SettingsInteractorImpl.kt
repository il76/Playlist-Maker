package com.il76.playlistmaker.settings.domain

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.il76.playlistmaker.settings.domain.api.SettingsInteractor
import com.il76.playlistmaker.settings.domain.models.ThemeSettings

class SettingsInteractorImpl(private val sharedPreferences: SharedPreferences, private val context: Context):
    SettingsInteractor {

    override fun saveThemeSettings(theme: ThemeSettings) {
        sharedPreferences.edit().putBoolean(DARK_THEME_ENABLED, theme.isDark).apply()
    }

    override fun switchTheme(theme: ThemeSettings) {
        AppCompatDelegate.setDefaultNightMode(
            if (theme.isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun getThemeSettings(): ThemeSettings {
        if (!sharedPreferences.contains(DARK_THEME_ENABLED)) {
            // вытаскиваем текущие настройки из системы
            return ThemeSettings(context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
        } else {
            return ThemeSettings(sharedPreferences.getBoolean(DARK_THEME_ENABLED,AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO))
        }
    }

    companion object {
        const val DARK_THEME_ENABLED = "dark_theme_enabled"
    }
}