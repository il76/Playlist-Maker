package com.il76.playlistmaker.search.data

import com.il76.playlistmaker.search.data.dto.TracksSearchRequest
import com.il76.playlistmaker.search.data.dto.TracksSearchResponse
import com.il76.playlistmaker.search.domain.api.TracksRepository
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.search.data.network.NetworkClient

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    // null пришлось добавить для возврата статуса ошибки
    override fun searchTracks(term: String): List<Track>? {
        val response = networkClient.doRequest(TracksSearchRequest(term))


        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
                Track(it.trackName, it.artistName, it.getTime(), it.artworkUrl100, it.trackId, it.collectionName, it.releaseDate, it.primaryGenreName, it.country, it.previewUrl, it.getPoster(), it.getReleaseYear()) }
        } else {
            return null
        }
    }
}