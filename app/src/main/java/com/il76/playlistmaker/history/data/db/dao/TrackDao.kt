package com.il76.playlistmaker.history.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.il76.playlistmaker.history.data.db.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM tracks_list WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Int)

    @Query("SELECT * FROM tracks_list ORDER BY id DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM tracks_list")
    suspend fun getTracksIds(): List<Int>
}