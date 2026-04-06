package com.example.playlistmaker.search.data.dto

/**
 * Объект-запрос (DTO) для поиска треков.
 * Содержит строку запроса (текст поиска), которая передается в API.
 */
data class TrackSearchRequest(
    val expression: String // Текст поискового запроса (название песни или автора)
)
