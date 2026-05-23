package com.example.playlistmaker.favorite.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.favorite.data.db.dao.FavoriteTracksDao
import com.example.playlistmaker.favorite.data.db.entity.TrackEntity


    @Database(version = 1, entities = [TrackEntity::class])
    abstract class AppDatabase : RoomDatabase() {
        abstract fun favoriteTracksDao(): FavoriteTracksDao
}