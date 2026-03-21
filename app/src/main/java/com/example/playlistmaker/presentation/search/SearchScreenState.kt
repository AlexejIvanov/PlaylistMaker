package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.Track

/**
 * Набор состояний экрана поиска для реализации реактивного UI.
 */
sealed class SearchScreenState {
    // Процесс загрузки (отображение ProgressBar)
    object Loading : SearchScreenState()

    // Результаты поиска найдены и готовы к отображению
    data class Content(val tracks: List<Track>) : SearchScreenState()

    // По запросу ничего не найдено
    object Empty : SearchScreenState()

    // Ошибка сети или сервера
    data class Error(val message: String) : SearchScreenState()

    // Отображение истории последних запросов
    data class History(val tracks: List<Track>) : SearchScreenState()
}