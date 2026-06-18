package com.example.playlistmaker.favorite.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.favorite.data.db.dao.FavoriteTracksDao
import com.example.playlistmaker.favorite.data.db.entity.TrackEntity
import com.example.playlistmaker.playlist.data.db.dao.PlaylistDao
import com.example.playlistmaker.playlist.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackEntity

@Database(
    version = 3,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}