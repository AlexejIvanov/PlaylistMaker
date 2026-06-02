package com.example.playlistmaker.favorite.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_tracks_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: Long,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val trackTimeMillis: Long,
    val addedTimestamp: Long, // системное время добавление для сортировки в избранном
)
