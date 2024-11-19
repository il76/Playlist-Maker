package com.il76.playlistmaker.data

import com.il76.playlistmaker.data.dto.TracksSearchRequest
import com.il76.playlistmaker.data.dto.TracksSearchResponse
import com.il76.playlistmaker.domain.api.TracksRepository
import com.il76.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(term: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(term))
        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
                Track(it.trackName, it.artistName, it.trackTime, it.artworkUrl100, it.trackId, it.collectionName, it.releaseDate, it.primaryGenreName, it.country, it.previewUrl) }
        } else {
            return emptyList()
        }
    }
}