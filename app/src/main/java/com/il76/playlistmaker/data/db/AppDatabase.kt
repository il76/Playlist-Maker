package com.il76.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.il76.playlistmaker.history.data.db.TrackEntity
import com.il76.playlistmaker.history.data.db.dao.TrackDao

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao

}