package com.example.playlistmaker.favorite.domain.db

import com.example.playlistmaker.core.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
): FavoriteTrackInteractor {
    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTrackRepository.addTrackToFavorites(track)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteTrackRepository.removeTrackFromFavorites(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.getFavoriteTracks()
    }

}