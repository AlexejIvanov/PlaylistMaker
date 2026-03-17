package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.ITunesResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTrack(expression: String, callback: (List<Track>?, String?) -> Unit) {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                callback(null, "Проверьте подключение к интернету")
            }

            200 -> {
                val iTunesResponse = response as ITunesResponse

                if (iTunesResponse.results.isNotEmpty()) {
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
                    callback(tracks, null)
                } else {
                    callback(emptyList(), null)
                }
            }

            else -> {
                callback(null, "Ошибка сервера")
            }
        }
    }
}
