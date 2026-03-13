package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun read(): List<Track>
    fun add(track: Track)
    fun clear()
}
