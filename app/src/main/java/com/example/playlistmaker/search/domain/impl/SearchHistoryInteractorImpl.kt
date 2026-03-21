package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.core.models.Track

/**
 * Реализация интерактора для управления историей поиска.
 * Вызывает соответствующие методы репозитория для работы с данными.
 */
class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository): SearchHistoryInteractor {

    // Получение списка ранее просмотренных треков
    override fun getHistory(): List<Track> = repository.read()

    // Добавление нового трека в список истории
    override fun addTrackToHistory(track: Track) = repository.add(track)

    // Полное удаление всех треков из истории поиска
    override fun clearHistory() = repository.clear()
}