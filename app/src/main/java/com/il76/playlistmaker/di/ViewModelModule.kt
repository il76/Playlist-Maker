package com.il76.playlistmaker.di

import com.il76.playlistmaker.player.ui.PlayerViewModel
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.search.ui.SearchViewModel
import com.il76.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get()
        )
    }
    
    viewModel { (track: Track) ->
        PlayerViewModel(
            track = track,
            playerInteractor = get()
        )
    }
    
    viewModel {
        SearchViewModel(
            trackInteractor = get(),
            tracksHistoryInteractor = get()
        )
    }
}