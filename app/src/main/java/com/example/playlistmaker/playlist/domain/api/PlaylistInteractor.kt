package com.example.playlistmaker.playlist.domain.api

import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist, imageUriString: String?): Long

    suspend fun addTrackToPlaylist(track: com.example.playlistmaker.core.models.Track, playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
}