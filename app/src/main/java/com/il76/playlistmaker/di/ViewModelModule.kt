package com.il76.playlistmaker.di

import com.il76.playlistmaker.history.presentation.HistoryViewModel
import com.il76.playlistmaker.media.ui.MediaViewModel
import com.il76.playlistmaker.media.ui.PlaylistAddViewModel
import com.il76.playlistmaker.media.ui.PlaylistsViewModel
import com.il76.playlistmaker.media.ui.TracksViewModel
import com.il76.playlistmaker.player.ui.PlayerViewModel
import com.il76.playlistmaker.search.ui.SearchViewModel
import com.il76.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get()
        )
    }
    
    viewModel { (trackData: String) ->
        PlayerViewModel(
            trackData,
            get(),
            get(),
            get()
        )
    }
    
    viewModel {
        SearchViewModel(
            trackInteractor = get(),
            tracksHistoryInteractor = get(),
            gson = get()
        )
    }
    viewModel {
        TracksViewModel(
            get(),
            get()
        )
    }

    viewModel {
        PlaylistsViewModel()
    }

    viewModel {
        HistoryViewModel(androidContext(), get())
    }

    viewModel {
        MediaViewModel()
    }
    viewModel {
        PlaylistAddViewModel()
    }
}