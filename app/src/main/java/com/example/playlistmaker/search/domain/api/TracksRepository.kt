package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.core.models.Track

/**
 * Интерфейс репозитория для работы с данными о треках.
 * Определяет контракт на выполнение поискового запроса.
 */
interface TracksRepository {
    // Выполняет поиск треков по строковому запросу (expression)
    // Результат возвращается через callback: список треков ИЛИ текст ошибки
    fun searchTrack(expression: String, callback: (List<Track>?, String?) -> Unit)
}