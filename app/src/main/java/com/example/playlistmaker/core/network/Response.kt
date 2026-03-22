package com.example.playlistmaker.core.network

/**
 * Базовый класс для сетевых ответов.
 * Позволяет хранить код результата (HTTP-статус или кастомный код ошибки).
 */
open class Response {
    var resultCode = 0 // Код ответа (например, 200 — успех, -1 — нет сети, 400 — ошибка)
}