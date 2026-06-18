package com.example.playlistmaker.playlist.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.favorite.data.db.AppDatabase
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val gson: Gson,
    private val context: Context
) : PlaylistRepository {

    override suspend fun savePlaylist(playlist: Playlist, imageUriString: String?): Long {
        var savedImagePath: String? = null

        if (!imageUriString.isNullOrEmpty()) {
            val uri = Uri.parse(imageUriString)
            val filePath =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
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

    override suspend fun addTrackToPlaylist(
        track: com.example.playlistmaker.core.models.Track,
        playlist: Playlist
    ) {

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
        val currentTrackIds: MutableList<Long> =
            gson.fromJson(currentPlaylistEntity.trackIds, type) ?: mutableListOf()

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

    override fun getPlaylistById(id: Int): Flow<Playlist> = flow {
        val entity = appDatabase.playlistDao().getPlaylistById(id)
        val type = object : TypeToken<List<Long>>() {}.type
        val tracksIds: List<Long> = gson.fromJson(entity.trackIds, type) ?: emptyList()

        val playlist = Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverFilePath = entity.coverFilePath,
            trackIds = tracksIds,
            trackCount = entity.trackCount
        )
        emit(playlist)
    }

    override fun getTracksFromPlaylist(trackIds: List<Long>): Flow<List<Track>> = flow {
        val allTracksEntity = appDatabase.playlistTrackDao().getAllTracks()

        // Возвращаем только те треки, идентификаторы которых есть в плейлисте
        val filteredTracks = allTracksEntity.filter { trackIds.contains(it.trackId) }

        // Ручной маппинг из PlaylistTrackEntity в Track с защитой от null
        val mappedTracks = filteredTracks.map { entity ->
            Track(
                trackId = entity.trackId,
                trackName = entity.trackName ?: "",
                artistName = entity.artistName ?: "",
                trackTimeMillis = entity.trackTimeMillis ?: 0L,
                artworkUrl100 = entity.artworkUrl100 ?: "",
                collectionName = entity.collectionName ?: "",
                releaseDate = entity.releaseDate ?: "",
                primaryGenreName = entity.primaryGenreName ?: "",
                country = entity.country ?: "",
                previewUrl = entity.previewUrl ?: ""
            )
        }
        emit(mappedTracks)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Int) {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId) ?: return
        val type = object : TypeToken<List<Long>>() {}.type
        val currentTrackIds: MutableList<Long> =
            gson.fromJson(playlistEntity.trackIds, type) ?: mutableListOf()

        if (currentTrackIds.contains(trackId)) {
            currentTrackIds.remove(trackId)
            val updatedPlaylistEntity = playlistEntity.copy(
                trackIds = gson.toJson(currentTrackIds),
                trackCount = currentTrackIds.size
            )
            appDatabase.playlistDao().updatePlaylist(updatedPlaylistEntity)
        }

        val allPlaylists = appDatabase.playlistDao().getPlaylists().first()
        var isTrackUsed = false

        for (p in allPlaylists) {
            val ids: List<Long> = gson.fromJson(p.trackIds, type) ?: emptyList()
            if (ids.contains(trackId)) {
                isTrackUsed = true
                break
            }
        }

        if (!isTrackUsed) {
            appDatabase.playlistTrackDao().deleteTrackById(trackId)
        }
    }

    override suspend fun deletePlaylist(id: Int) {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(id)
        val type = object : TypeToken<List<Long>>() {}.type
        val trackIds: List<Long> = gson.fromJson(playlistEntity.trackIds, type) ?: emptyList()

        appDatabase.playlistDao().deletePlaylist(id)

        val allPlaylists = appDatabase.playlistDao().getPlaylists().first()
        for (trackId in trackIds) {
            var isTrackUsed = false
            for (p in allPlaylists) {
                val pIds: List<Long> = gson.fromJson(p.trackIds, type) ?: emptyList()
                if (pIds.contains(trackId)) {
                    isTrackUsed = true
                    break
                }
            }
            if (!isTrackUsed) {
                appDatabase.playlistTrackDao().deleteTrackById(trackId)
            }
        }
    }

    override suspend fun updatePlaylistDetails(
        id: Int,
        name: String,
        description: String?,
        imageUriString: String?
    ) {
        val existing = appDatabase.playlistDao().getPlaylistById(id) ?: return
        var newCoverPath = existing.coverFilePath

        // Если imageUriString не пуст и отличается от старого пути, значит выбрана новая картинка
        if (!imageUriString.isNullOrEmpty() && imageUriString != existing.coverFilePath) {
            val uri = Uri.parse(imageUriString)
            val filePath =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
            if (!filePath.exists()) filePath.mkdirs()

            val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            newCoverPath = file.absolutePath
        }

        val updatedEntity = existing.copy(
            name = name,
            description = description,
            coverFilePath = newCoverPath
        )
        appDatabase.playlistDao().updatePlaylist(updatedEntity)
    }
}

