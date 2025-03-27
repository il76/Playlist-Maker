package com.il76.playlistmaker.media.data

import com.il76.playlistmaker.data.converters.PlaylistDbConverter
import com.il76.playlistmaker.data.converters.PlaylistTrackDbConverter
import com.il76.playlistmaker.data.db.AppDatabase
import com.il76.playlistmaker.data.db.InsertStatus
import com.il76.playlistmaker.media.data.db.PlaylistEntity
import com.il76.playlistmaker.media.domain.api.PlaylistRepository
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter
): PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun addTrackToPlaylist(playlistTrack: PlaylistTrack): InsertStatus {
        val entity = playlistTrackDbConverter.map(playlistTrack)
        return if (appDatabase.playlistTrackDao().exists(entity.playlistId, entity.trackId) > 0) {
            InsertStatus.ALREADY_EXISTS
        } else {
            val result = appDatabase.playlistTrackDao().insert(entity)
            if (result == -1L) InsertStatus.FAILED else InsertStatus.SUCCESS
        }
    }



    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

}