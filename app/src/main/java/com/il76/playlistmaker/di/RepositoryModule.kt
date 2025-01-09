package com.il76.playlistmaker.di

import com.il76.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.il76.playlistmaker.player.domain.api.MediaPlayerRepository
import com.il76.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.il76.playlistmaker.search.data.TracksRepositoryImpl
import com.il76.playlistmaker.search.domain.api.TracksHistoryRepository
import com.il76.playlistmaker.search.domain.api.TracksRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(
            sharedPreferences = get(),
            gson = get()
        )
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(
            networkClient = get()
        )
    }

    factory <MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(
            player = get()
        )
    }

}