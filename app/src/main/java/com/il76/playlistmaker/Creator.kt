package com.il76.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.il76.playlistmaker.application.App.Companion.PLAYLIST_MAKER_PREFERENCES
import com.il76.playlistmaker.data.TracksRepositoryImpl
import com.il76.playlistmaker.data.network.RetrofitNetworkClient
import com.il76.playlistmaker.domain.api.TracksInteractor
import com.il76.playlistmaker.domain.api.TracksRepository
import com.il76.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }



    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}