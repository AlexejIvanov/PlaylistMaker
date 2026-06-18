package com.example.playlistmaker.favorite.domain.db

import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.favorite.data.db.AppDatabase
import com.example.playlistmaker.favorite.data.db.TrackDbConvertor
import com.example.playlistmaker.favorite.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl
    (
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,

    ) : FavoriteTrackRepository {
    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.favoriteTracksDao().insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.favoriteTracksDao().deleteTrack(trackEntity)

    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTracksDao().getAllTracks().map { tracks ->
            convertFromTrackEntity(tracks)
        }
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { trackEntity -> trackDbConvertor.map(trackEntity) }
    }


}