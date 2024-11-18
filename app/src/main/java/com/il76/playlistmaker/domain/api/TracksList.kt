package com.il76.playlistmaker.domain.api

import com.il76.playlistmaker.domain.models.Track

data class TracksList (
    val resultCount: Int = 0,
    val results: ArrayList<Track> = arrayListOf()
)