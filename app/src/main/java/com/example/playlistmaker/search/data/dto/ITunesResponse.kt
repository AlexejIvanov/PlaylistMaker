package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.core.network.Response

/**
 * DTO (Data Transfer Object) для ответа от iTunes Search API.
 * Наследуется от Response для хранения кода результата (resultCode).
 */
data class ITunesResponse(
    val resultCount: Int, // Общее количество найденных треков в ответе
    val results: List<TrackDto>, // Список объектов с данными о каждом треке
) : Response()
