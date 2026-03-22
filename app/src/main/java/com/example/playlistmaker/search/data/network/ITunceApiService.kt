package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.ITunesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Интерфейс для работы с iTunes Search API через Retrofit.
 */
interface ITunesApiService {
    // Выполняет GET-запрос к эндпоинту /search, фильтруя результаты только по песням (entity=song)
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<ITunesResponse> // @Query("term") подставляет текст поиска в URL
}
