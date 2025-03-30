package com.il76.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.il76.playlistmaker.history.data.db.TrackEntity
import com.il76.playlistmaker.media.data.db.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("""
        SELECT p.*, 
               (SELECT COUNT(*) FROM playlists_tracks WHERE playlistId = p.id) AS cnt
        FROM playlists p
        ORDER BY p.id DESC
    """)
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getSinglePlaylist(playlistId: Int): PlaylistEntity

}