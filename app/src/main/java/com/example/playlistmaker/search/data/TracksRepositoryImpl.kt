package com.example.playlistmaker.search.data

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.core.network.NetworkClient
import com.example.playlistmaker.favorite.data.db.AppDatabase
import com.example.playlistmaker.search.data.dto.ITunesResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.domain.api.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Реализация репозитория для поиска треков.
 * Преобразует сетевые данные (DTO) в доменные модели (Track).
 */
class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
) : TracksRepository {

    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow {
        // Отправка запроса через сетевой клиент
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            -1 -> { // Нет подключения к сети
                emit(Resource.Error( "Проверьте подключение к интернету"))
            }

            200 -> { // Успешный ответ от сервера
                val favoriteIds = appDatabase.favoriteTracksDao().getAllIds()
                val data = (response as ITunesResponse).results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl ?: "",
                        isFavorite = favoriteIds.contains(it.trackId)
                    )
                }
                emit(Resource.Success(data))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}
