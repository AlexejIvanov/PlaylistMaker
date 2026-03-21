package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

/**
 * Интерфейс для управления историей поиска треков (бизнес-логика).
 */
interface SearchHistoryInteractor {
    fun getHistory(): List<Track>          // Возвращает список сохраненных в истории треков
    fun addTrackToHistory (track: Track)   // Добавляет выбранный трек в историю (с учетом лимита и дубликатов)
    fun clearHistory()                     // Полностью очищает список истории
}