package com.il76.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.il76.playlistmaker.media.data.db.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlistTrack: PlaylistTrackEntity)

    @Delete
    suspend fun delete(playlistTrack: PlaylistTrackEntity)
}