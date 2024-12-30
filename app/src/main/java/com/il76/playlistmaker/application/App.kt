package com.il76.playlistmaker.application

import android.app.Application
import com.il76.playlistmaker.creator.Creator

class App : Application() {

    // без by lazy нарушается последовательность создания
    private val settingsInteractor by lazy { Creator.provideSettingsInteractor()}

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        settingsInteractor.switchTheme(settingsInteractor.getThemeSettings())
    }
}