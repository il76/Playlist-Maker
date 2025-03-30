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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM tracks_list WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Int)

    @Query("SELECT * FROM tracks_list WHERE isFavourite = 1 ORDER BY id DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM tracks_list")
    suspend fun getTracksIds(): List<Int>

    @Query("""
        SELECT tl.* FROM playlists_tracks AS plt
         INNER JOIN tracks_list AS tl ON tl.trackId = plt.trackId  
        WHERE plt.playlistId = :playlistId
        ORDER BY plt.trackId DESC
    """)
    suspend fun getPlaylistTracks(playlistId: Int): List<TrackEntity>
}