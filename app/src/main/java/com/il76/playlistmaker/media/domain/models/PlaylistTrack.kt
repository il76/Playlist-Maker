package com.il76.playlistmaker.media.domain.models

import com.il76.playlistmaker.search.domain.models.Track

data class PlaylistTrack(val playlist: Playlist, val track: Track)