package com.example.playlistmaker.favorite.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.playlistmaker.favorite.data.db.entity.TrackEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao {

    @Insert
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM favorite_tracks_table ORDER BY addedTimestamp DESC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorite_tracks_table")
    suspend fun getAllIds(): List<Long>

}