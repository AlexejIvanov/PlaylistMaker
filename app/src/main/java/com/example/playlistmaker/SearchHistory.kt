package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(val sharedPreferences: SharedPreferences) {

    // Чтение истории поиска из SharedPreferences
    fun read(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        return Gson().fromJson(json, Array<Track>::class.java).toList()
    }

    fun write(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    fun clear() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    fun add(track: Track) {
        //  Получаем текущий список.
        val history = read().toCollection(ArrayList())

        // Удаляем дубликат.
        val existingIndex = history.indexOfFirst { it.trackId == track.trackId }
        if (existingIndex != -1) {
            history.removeAt(existingIndex)
        }

        //  Добавляем новый трек в начало списка.
        history.add(0, track)

        // Проверяем размер списка.
        if (history.size > 10) {
            history.removeAt(10)
        }

        // Сохраняем обновленный список.
        write(history)
    }

    companion object {
        const val HISTORY_KEY = "HISTORY_KEY"
    }
}
