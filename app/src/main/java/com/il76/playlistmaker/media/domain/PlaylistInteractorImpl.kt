package com.il76.playlistmaker.media.domain

import com.il76.playlistmaker.data.db.InsertStatus
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.api.PlaylistRepository
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository): PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>?> {
        return repository.getPlaylists()
    }

    override fun getSinglePlaylist(playlistInd: Int): Flow<Playlist> {
        return repository.getSinglePlaylist(playlistInd)
    }

    override suspend fun createPlaylist(playlist: Playlist) {
        repository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlistTrack: PlaylistTrack): InsertStatus {
        return repository.addTrackToPlaylist(playlistTrack)
    }

    override suspend fun deleteTrackFromPlaylist(playlistTrack: PlaylistTrack) {
        return repository.deleteTrackFromPlaylist(playlistTrack)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }
}