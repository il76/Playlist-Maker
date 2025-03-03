package com.il76.playlistmaker.history.data.db

import com.il76.playlistmaker.data.converters.TrackDbConverter
import com.il76.playlistmaker.data.db.AppDatabase
import com.il76.playlistmaker.history.domain.db.HistoryRepository
import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : HistoryRepository {

    override fun historyTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }

    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun delTrack(track: Track) {
        appDatabase.trackDao().deleteTrackById(track.trackId)
    }
}