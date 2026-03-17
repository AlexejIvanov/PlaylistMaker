package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import androidx.core.content.edit

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    companion object {
        const val HISTORY_KEY = "HISTORY_KEY"
    }

    override fun read(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    override fun add(track: Track) {
        val history = read().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > 10) history.removeAt(10)

        sharedPreferences.edit {
            putString(HISTORY_KEY, gson.toJson(history))
        }
    }

    override fun clear() {
        sharedPreferences.edit { remove(HISTORY_KEY) }
    }
}
