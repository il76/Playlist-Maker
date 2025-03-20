package com.il76.playlistmaker.media.domain.api

import com.il76.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>?>
}
