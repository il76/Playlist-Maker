package com.il76.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.il76.playlistmaker.player.data.MediaPlayerInteractorImpl
import com.il76.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.il76.playlistmaker.settings.data.SettingsInteractorImpl
import com.il76.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.il76.playlistmaker.search.data.TracksRepositoryImpl
import com.il76.playlistmaker.search.data.network.RetrofitNetworkClient
import com.il76.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.player.domain.api.MediaPlayerRepository
import com.il76.playlistmaker.settings.domain.api.SettingsInteractor
import com.il76.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.il76.playlistmaker.search.domain.api.TracksHistoryRepository
import com.il76.playlistmaker.search.domain.api.TracksInteractor
import com.il76.playlistmaker.search.domain.api.TracksRepository
import com.il76.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.il76.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.il76.playlistmaker.sharing.api.ExternalNavigator
import com.il76.playlistmaker.sharing.api.SharingInteractor
import com.il76.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.il76.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    private lateinit var application: Application

    private val gson = Gson()

    fun initApplication(application: Application) {
        Creator.application = application
    }
    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    //треки из сети

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    //история поиска

    private fun getTracksHistoryRepository(): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(provideSharedPreferences())
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository())
    }

    /**
     * Настройки
     */
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSharedPreferences(), application.applicationContext)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(provideExternalNavigator(), application.applicationContext)
    }

    private fun provideExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(application.applicationContext)
    }

    fun provideGson(): Gson {
        return gson
    }

    private fun getMediaPlayerRepository(player: MediaPlayer): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl(player)
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository(MediaPlayer()))
    }

    private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

}