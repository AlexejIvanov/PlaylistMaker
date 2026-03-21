package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.ITunesResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

/**
 * Реализация репозитория для поиска треков.
 * Преобразует сетевые данные (DTO) в доменные модели (Track).
 */
class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTrack(expression: String, callback: (List<Track>?, String?) -> Unit) {
        // Отправка запроса через сетевой клиент
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            -1 -> { // Нет подключения к сети
                callback(null, "Проверьте подключение к интернету")
            }

            200 -> { // Успешный ответ от сервера
                val iTunesResponse = response as ITunesResponse

                if (iTunesResponse.results.isNotEmpty()) {
                    // Маппинг: превращаем список TrackDto в список Track
                    val tracks = iTunesResponse.results.map { dto ->
                        Track(
                            trackId = dto.trackId,
                            trackName = dto.trackName,
                            artistName = dto.artistName,
                            trackTimeMillis = dto.trackTimeMillis,
                            artworkUrl100 = dto.artworkUrl100,
                            collectionName = dto.collectionName,
                            releaseDate = dto.releaseDate,
                            primaryGenreName = dto.primaryGenreName,
                            country = dto.country,
                            previewUrl = dto.previewUrl ?: ""
                        )
                    }
                    callback(tracks, null) // Возвращаем найденные треки
                } else {
                    callback(emptyList(), null) // Треки не найдены (пустой список)
                }
            }

            else -> { // Ошибки 400, 500 и прочие
                callback(null, "Ошибка сервера")
            }
        }
    }
}