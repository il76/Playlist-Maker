package com.il76.playlistmaker.history.data.db

import com.il76.playlistmaker.data.converters.TrackDbConverter
import com.il76.playlistmaker.data.db.AppDatabase
import com.il76.playlistmaker.history.domain.db.HistoryRepository
import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val movieDbConvertor: TrackDbConverter,
) : HistoryRepository {

    override fun historyTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromMovieEntity(tracks))
    }

    private fun convertFromMovieEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> movieDbConvertor.map(track) }
    }
}