package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

/**
 * Интерфейс репозитория для работы с хранилищем истории поиска.
 * Описывает контракт для сохранения и получения данных в слое Data.
 */
interface SearchHistoryRepository {
    fun read(): List<Track>    // Получить список сохраненных треков из хранилища
    fun add(track: Track)      // Сохранить новый трек в историю
    fun clear()                // Удалить все записи из истории поиска
}