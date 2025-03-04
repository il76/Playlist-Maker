package com.il76.playlistmaker.search.data

import com.il76.playlistmaker.data.db.AppDatabase
import com.il76.playlistmaker.search.data.dto.TracksSearchRequest
import com.il76.playlistmaker.search.data.dto.TracksSearchResponse
import com.il76.playlistmaker.search.domain.api.TracksRepository
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.search.data.network.NetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient,
                           private val appDatabase: AppDatabase) : TracksRepository {

    // null пришлось добавить для возврата статуса ошибки
    override fun searchTracks(term: String): Flow<List<Track>?> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(term))
        if (response.resultCode == 200) {
            val favouriteTracks = appDatabase.trackDao().getTracksIds()
            with(response as TracksSearchResponse) {
                val data = results.map {
                    Track(
                        it.id,
                        it.trackName,
                        it.artistName,
                        it.getTime(),
                        it.artworkUrl100,
                        it.trackId,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl,
                        it.getPoster(),
                        it.getReleaseYear(),
                        it.trackId in favouriteTracks
                    )
                }
                emit(data)
            }
        } else {
            emit(null)
        }
    }
}