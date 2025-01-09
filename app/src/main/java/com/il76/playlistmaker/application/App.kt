package com.il76.playlistmaker.application

import android.app.Application
import com.il76.playlistmaker.di.dataModule
import com.il76.playlistmaker.di.interactorModule
import com.il76.playlistmaker.di.repositoryModule
import com.il76.playlistmaker.di.viewModelModule
import com.il76.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        val settingsInteractor: SettingsInteractor by inject()
        settingsInteractor.switchTheme(settingsInteractor.getThemeSettings())
    }
}