package com.il76.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.il76.playlistmaker.application.App.Companion.PLAYLIST_MAKER_PREFERENCES
import com.il76.playlistmaker.data.MediaPlayerInteractorImpl
import com.il76.playlistmaker.data.SettingsInteractorImpl
import com.il76.playlistmaker.data.TracksRepositoryImpl
import com.il76.playlistmaker.data.network.RetrofitNetworkClient
import com.il76.playlistmaker.domain.api.MediaPlayerInteractor
import com.il76.playlistmaker.domain.api.SettingsInteractor
import com.il76.playlistmaker.domain.api.TracksInteractor
import com.il76.playlistmaker.domain.api.TracksRepository
import com.il76.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private lateinit var application: Application

    private val gson = Gson()

    fun initApplication(application: Application) {
        this.application = application
    }
    //private
     fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }



    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    /**
     * Настройки
     */
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSharedPreferences())
    }

    fun provideGson(): Gson {
        return gson
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(MediaPlayer())
    }

}