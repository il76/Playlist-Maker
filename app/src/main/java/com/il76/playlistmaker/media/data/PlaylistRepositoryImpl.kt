package com.il76.playlistmaker.media.data

import android.util.Log
import com.il76.playlistmaker.data.converters.PlaylistDbConverter
import com.il76.playlistmaker.data.converters.PlaylistTrackDbConverter
import com.il76.playlistmaker.data.converters.TrackDbConverter
import com.il76.playlistmaker.data.db.AppDatabase
import com.il76.playlistmaker.data.db.InsertStatus
import com.il76.playlistmaker.media.data.db.PlaylistEntity
import com.il76.playlistmaker.media.domain.api.PlaylistRepository
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.log

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter,
    private val trackDbConverter: TrackDbConverter
): PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun addTrackToPlaylist(playlistTrack: PlaylistTrack): InsertStatus {
        val entity = playlistTrackDbConverter.map(playlistTrack)
        return if (appDatabase.playlistTrackDao().exists(entity.playlistId, entity.trackId) > 0) {
            InsertStatus.ALREADY_EXISTS
        } else {
            appDatabase.trackDao().insertTrack(trackDbConverter.map(playlistTrack.track))
            val result = appDatabase.playlistTrackDao().insert(entity)
            if (result == -1L) InsertStatus.FAILED else InsertStatus.SUCCESS
        }
    }

    override suspend fun deleteTrackFromPlaylist(playlistTrack: PlaylistTrack) {
        appDatabase.playlistTrackDao().delete(playlistTrackDbConverter.map(playlistTrack))
        Log.i("pls", playlistTrack.track.isFavourite.toString())
        if (!playlistTrack.track.isFavourite) { //трек не в избранных, ищем, нужен ли он вообще

            if (appDatabase.playlistTrackDao().getTrackUsageCnt(playlistTrack.track.trackId) == 0) { //трек больше не нужен
                appDatabase.trackDao().deleteTrackById(playlistTrack.track.trackId)
            }
        }
    }


    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlistDbConverter.map(playlist))
    }



    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override fun getSinglePlaylist(playlistId: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistDao().getSinglePlaylist(playlistId)
        emit(playlistDbConverter.map(playlist))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

}