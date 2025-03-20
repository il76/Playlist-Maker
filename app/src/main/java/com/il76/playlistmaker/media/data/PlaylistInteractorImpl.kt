package com.il76.playlistmaker.media.data

import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepositoryImpl): PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>?> {
        //return repository.getPlaylists()
        TODO()
    }
}