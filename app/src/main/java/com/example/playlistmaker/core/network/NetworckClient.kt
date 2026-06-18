package com.example.playlistmaker.core.network

/**
 * Интерфейс сетевого клиента (абстракция над Retrofit).
 * Позволяет репозиториям выполнять запросы, не зная деталей реализации сети.
 */
interface NetworkClient {
    // Выполняет сетевой запрос на основе переданного DTO и возвращает базовый Response
    suspend fun doRequest(dto: Any): Response
}