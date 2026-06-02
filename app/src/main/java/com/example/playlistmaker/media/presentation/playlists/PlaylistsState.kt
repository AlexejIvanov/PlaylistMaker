package com.example.playlistmaker.media.presentation.playlists

import com.example.playlistmaker.playlist.domain.models.Playlist

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
}