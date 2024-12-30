package com.il76.playlistmaker.search.data.network

import com.il76.playlistmaker.search.data.dto.Response
import com.il76.playlistmaker.search.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {
    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(TrackAPIService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            try {
                val resp = itunesService.getTracks(dto.term).execute()
                val body = resp.body() ?: Response()
                return body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                return Response().apply { resultCode = 0 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}