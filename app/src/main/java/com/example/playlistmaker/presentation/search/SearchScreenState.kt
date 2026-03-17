package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.Track

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    data class Content(val tracks: List<Track>) : SearchScreenState()
    object Empty : SearchScreenState()
    data class Error(val message: String) : SearchScreenState()
    data class History(val tracks: List<Track>) : SearchScreenState()
}
