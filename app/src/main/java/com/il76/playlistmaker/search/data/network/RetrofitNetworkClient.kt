package com.il76.playlistmaker.search.data.network

import com.il76.playlistmaker.search.data.dto.Response
import com.il76.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val itunesService: TrackAPIService): NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            withContext(Dispatchers.IO) {
                try {
                    val resp = itunesService.getTracks(dto.term)
                    resp.apply { resultCode = 200 }
                } catch (e: Exception) {
                    Response().apply { resultCode = 0 }
                }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}