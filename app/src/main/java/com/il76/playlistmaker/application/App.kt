package com.il76.playlistmaker.application

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.il76.playlistmaker.Creator

class App : Application() {

    // без by lazy нарушается последовательность создания
    private val settingsInteractor by lazy {Creator.provideSettingsInteractor()}

    override fun onCreate() {
        super.onCreate()
        instance = this //чтобы обращаться к свойствам класса снаружи
        Creator.initApplication(this)
        settingsInteractor.switchTheme(settingsInteractor.getThemeSettings())
    }

    /**
     * Унифицированное логирование
     */
    fun log(message: String) {
        Log.i("pls", message)
    }

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val TRACKS_SEARCH_HISTORY = "tracks_search_history"
        lateinit var instance: App
            private set
    }
}