package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.ITunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Интерфейс для работы с iTunes Search API через Retrofit.
 */
interface ITunesApiService {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): ITunesResponse
}
