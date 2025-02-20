package com.il76.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room.databaseBuilder
import com.google.gson.Gson
import com.il76.playlistmaker.data.db.AppDatabase
import com.il76.playlistmaker.search.data.network.NetworkClient
import com.il76.playlistmaker.search.data.network.RetrofitNetworkClient
import com.il76.playlistmaker.search.data.network.TrackAPIService
import com.il76.playlistmaker.sharing.api.ExternalNavigator
import com.il76.playlistmaker.sharing.data.ExternalNavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single {
        androidContext()
            .getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
    }
    factory {
        Gson()
    }

    single<TrackAPIService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackAPIService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    factory {
        MediaPlayer()
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(
            context = get()
        )
    }

    single {
        databaseBuilder(androidContext(), AppDatabase::class.java, "playlistmaker.db")
            .build()
    }

}