package com.il76.playlistmaker.search.data.network

import com.il76.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackAPIService {
    @GET("search?entity=musicTrack")
    fun getTracks(@Query("term", encoded = false) text: String): Call<TracksSearchResponse>
}