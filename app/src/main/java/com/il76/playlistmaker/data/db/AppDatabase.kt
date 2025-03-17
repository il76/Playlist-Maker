package com.il76.playlistmaker.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.il76.playlistmaker.media.data.db.PlaylistEntity
import com.il76.playlistmaker.history.data.db.TrackEntity
import com.il76.playlistmaker.media.data.db.dao.PlaylistDao
import com.il76.playlistmaker.history.data.db.dao.TrackDao

@Database(
    version = 2,
    entities = [TrackEntity::class, PlaylistEntity::class],
    autoMigrations = [
        AutoMigration (
            from = 1,
            to = 2
        ),
    ],
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

}