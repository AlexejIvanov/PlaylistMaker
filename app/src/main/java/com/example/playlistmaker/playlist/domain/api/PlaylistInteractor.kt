package com.example.playlistmaker.playlist.domain.api

import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist, imageUriString: String?): Long

    suspend fun addTrackToPlaylist(
        track: com.example.playlistmaker.core.models.Track,
        playlist: Playlist
    )

    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: Int): Flow<Playlist>
    fun getTracksFromPlaylist(trackIds: List<Long>): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Int)
    suspend fun deletePlaylist(id: Int)
    suspend fun updatePlaylistDetails(
        id: Int,
        name: String,
        description: String?,
        imageUriString: String?
    )
}