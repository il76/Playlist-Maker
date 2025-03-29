package com.il76.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.il76.playlistmaker.media.data.db.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Delete
    suspend fun delete(playlistTrack: PlaylistTrackEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playlistTrack: PlaylistTrackEntity): Long

    @Query("SELECT COUNT(*) FROM playlists_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun exists(playlistId: Int, trackId: Int): Int


}