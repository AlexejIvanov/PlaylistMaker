package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import androidx.core.content.edit

/**
 * Реализация хранилища истории поиска с использованием SharedPreferences и JSON.
 */
class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    companion object {
        const val HISTORY_KEY = "HISTORY_KEY" // Ключ для хранения списка в настройках
    }

    // Читает список треков из JSON, если данных нет — возвращает пустой список
    override fun read(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    // Добавляет трек в историю: убирает дубликат, ставит в начало, ограничивает список 10 треками
    override fun add(track: Track) {
        val history = read().toMutableList()
        history.removeIf { it.trackId == track.trackId } // Удаление существующего такого же трека
        history.add(0, track) // Добавление нового в начало списка
        if (history.size > 10) history.removeAt(10) // Лимит — 10 элементов

        sharedPreferences.edit {
            putString(HISTORY_KEY, gson.toJson(history)) // Сохранение обновленного списка в JSON
        }
    }

    // Полная очистка истории поиска
    override fun clear() {
        sharedPreferences.edit { remove(HISTORY_KEY) }
    }
}