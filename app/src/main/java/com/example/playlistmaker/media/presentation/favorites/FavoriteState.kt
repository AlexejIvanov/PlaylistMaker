package com.example.playlistmaker.media.presentation.favorites

import com.example.playlistmaker.core.models.Track

/**
 * Состояния экрана "Избранные треки".
 */
sealed interface FavoriteState {
    object Empty : FavoriteState
    data class Content(val tracks: List<Track>) : FavoriteState
}
