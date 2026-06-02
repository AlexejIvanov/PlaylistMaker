package com.example.playlistmaker.playlist.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.favorite.data.db.AppDatabase
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val gson: Gson,
    private val context: Context
) : PlaylistRepository {

    override suspend fun savePlaylist(playlist: Playlist, imageUriString: String?): Long {
        var savedImagePath: String? = null

        if (!imageUriString.isNullOrEmpty()) {
            val uri = Uri.parse(imageUriString)
            val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            savedImagePath = file.absolutePath
        }

        val entity = PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            coverFilePath = savedImagePath,
            trackIds = gson.toJson(emptyList<Long>()),
            trackCount = 0
        )
        return appDatabase.playlistDao().insertPlaylist(entity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { entities ->
            entities.map { entity ->
                val type = object : TypeToken<List<Long>>() {}.type
                val tracksIds: List<Long> = gson.fromJson(entity.trackIds, type) ?: emptyList()

                Playlist(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    coverFilePath = entity.coverFilePath,
                    trackIds = tracksIds,
                    trackCount = entity.trackCount
                )
            }
        }
    }

    override suspend fun addTrackToPlaylist(track: com.example.playlistmaker.core.models.Track, playlist: Playlist) {

        val trackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        appDatabase.playlistTrackDao().insertTrack(trackEntity)

        val currentPlaylistEntity = appDatabase.playlistDao().getPlaylistById(playlist.id) ?: return

        val type = object : TypeToken<List<Long>>() {}.type
        val currentTrackIds: MutableList<Long> = gson.fromJson(currentPlaylistEntity.trackIds, type) ?: mutableListOf()

        val trackIdLong = track.trackId

        if (!currentTrackIds.contains(trackIdLong)) {
            currentTrackIds.add(trackIdLong)

            val updatedPlaylistEntity = currentPlaylistEntity.copy(
                trackIds = gson.toJson(currentTrackIds),
                trackCount = currentTrackIds.size
            )

            appDatabase.playlistDao().updatePlaylist(updatedPlaylistEntity)
        }
    }
}
