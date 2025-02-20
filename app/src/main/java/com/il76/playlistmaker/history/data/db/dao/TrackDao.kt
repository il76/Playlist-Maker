package com.il76.playlistmaker.history.data.db.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.il76.playlistmaker.history.data.db.TrackEntity

interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(movies: List<TrackEntity>)

    @Query("SELECT * FROM tracks_list")
    suspend fun getTracks(): List<TrackEntity>
}