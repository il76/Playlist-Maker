package com.il76.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.il76.playlistmaker.media.data.db.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("""
        SELECT p.id, p.name, p.description, p.cover, 
               (SELECT COUNT(*) FROM playlists_tracks WHERE playlistId = p.id) AS cnt
        FROM playlists p
        ORDER BY p.id DESC
    """)
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getSinglePlaylist(playlistId: Int): PlaylistEntity

}