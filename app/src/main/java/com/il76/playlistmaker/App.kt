package com.il76.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false

    val trackListHistory = arrayListOf<Track>()

    lateinit var trackSearchHistory: TrackSearchHistory

    fun clearHistory() {
        trackSearchHistory.clear()
    }

    fun putToHistory(track: Track) {
        trackSearchHistory.addElement(track)
    }

    lateinit var sharedPrefs: SharedPreferences

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val DARK_THEME_ENABLED = "dark_theme_enabled"
        const val TRACKS_SEARCH_HISTORY = "tracks_search_history"
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_ENABLED,AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
        switchTheme(darkTheme)
        instance = this //чтобы обращаться к свойствам класса снаружи
        trackSearchHistory = TrackSearchHistory(sharedPrefs)
        trackSearchHistory.loadFromPreferences()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}