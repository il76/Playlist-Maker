package com.il76.playlistmaker.media.domain.api

import com.il76.playlistmaker.data.db.InsertStatus
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(playlistTrack: PlaylistTrack): InsertStatus
    suspend fun deleteTrackFromPlaylist(playlistTrack: PlaylistTrack)
    fun getPlaylists(): Flow<List<Playlist>>
    fun getSinglePlaylist(playlistId: Int): Flow<Playlist>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
}