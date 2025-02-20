package com.il76.playlistmaker.di

import com.il76.playlistmaker.data.converters.TrackDbConverter
import com.il76.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.il76.playlistmaker.player.domain.api.MediaPlayerRepository
import com.il76.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.il76.playlistmaker.search.data.TracksRepositoryImpl
import com.il76.playlistmaker.history.data.db.HistoryRepositoryImpl
import com.il76.playlistmaker.search.domain.api.TracksHistoryRepository
import com.il76.playlistmaker.search.domain.api.TracksRepository
import com.il76.playlistmaker.history.domain.db.HistoryRepository
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
            networkClient = get(), get(), get()
        )
    }

    factory <MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(
            player = get()
        )
    }

    factory { TrackDbConverter() }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get(), get())
    }

}