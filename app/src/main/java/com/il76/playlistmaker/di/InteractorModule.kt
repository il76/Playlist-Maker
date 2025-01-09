package com.il76.playlistmaker.di

import com.il76.playlistmaker.player.data.MediaPlayerInteractorImpl
import com.il76.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.il76.playlistmaker.search.domain.api.TracksInteractor
import com.il76.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.il76.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.il76.playlistmaker.settings.data.SettingsInteractorImpl
import com.il76.playlistmaker.settings.domain.api.SettingsInteractor
import com.il76.playlistmaker.sharing.api.SharingInteractor
import com.il76.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<TracksInteractor> {
        TracksInteractorImpl(
            repository = get()
        )
    }

    factory<TracksHistoryInteractor> {
        TracksHistoryInteractorImpl(
            repository = get()
        )
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(
            playerRepository = get()
        )
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(
            sharedPreferences = get(),
            context = get()
        )
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(
            externalNavigator = get()
        )
    }

}