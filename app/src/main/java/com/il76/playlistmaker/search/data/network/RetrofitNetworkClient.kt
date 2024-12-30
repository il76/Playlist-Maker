package com.il76.playlistmaker.search.data.network

import com.il76.playlistmaker.search.data.dto.Response
import com.il76.playlistmaker.search.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val itunesService: TrackAPIService): NetworkClient {
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