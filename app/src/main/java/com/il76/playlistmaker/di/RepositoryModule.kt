package com.il76.playlistmaker.di

import com.il76.playlistmaker.data.converters.PlaylistDbConverter
import com.il76.playlistmaker.data.converters.PlaylistTrackDbConverter
import com.il76.playlistmaker.data.converters.TrackDbConverter
import com.il76.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.il76.playlistmaker.player.domain.api.MediaPlayerRepository
import com.il76.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.il76.playlistmaker.search.data.TracksRepositoryImpl
import com.il76.playlistmaker.history.data.db.HistoryRepositoryImpl
import com.il76.playlistmaker.search.domain.api.TracksHistoryRepository
import com.il76.playlistmaker.search.domain.api.TracksRepository
import com.il76.playlistmaker.history.domain.db.HistoryRepository
import com.il76.playlistmaker.media.data.PlaylistRepositoryImpl
import com.il76.playlistmaker.media.domain.api.PlaylistRepository
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
            networkClient = get(), get()
        )
    }

    factory <MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(
            player = get()
        )
    }

    factory { TrackDbConverter() }
    factory { PlaylistDbConverter() }
    factory { PlaylistTrackDbConverter() }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get(), get())
    }


    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(
            appDatabase = get(),
            playlistDbConverter = get(),
            playlistTrackDbConverter = get()
        )
    }

}