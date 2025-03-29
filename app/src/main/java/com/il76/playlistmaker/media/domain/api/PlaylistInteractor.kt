package com.il76.playlistmaker.media.domain.api

import com.il76.playlistmaker.data.db.InsertStatus
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>?>
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(playlistTrack: PlaylistTrack): InsertStatus
}
