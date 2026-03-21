package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.Response

/**
 * Интерфейс сетевого клиента (абстракция над Retrofit).
 * Позволяет репозиториям выполнять запросы, не зная деталей реализации сети.
 */
interface NetworkClient {
    // Выполняет сетевой запрос на основе переданного DTO и возвращает базовый Response
    fun doRequest(dto: Any): Response
}