package com.example.playlistmaker.playlist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String?,
    val coverFilePath: String?,
    val trackIds: String, // Список ID треков в формате JSON
    val trackCount: Int
)