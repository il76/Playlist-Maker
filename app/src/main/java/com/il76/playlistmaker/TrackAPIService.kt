package com.il76.playlistmaker

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackAPIService {
    @GET("search")
    fun getTracks(@Query("term", encoded = false) text: String): Call<TracksList>
}