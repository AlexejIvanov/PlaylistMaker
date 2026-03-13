package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository): SearchHistoryInteractor {
    override fun getHistory(): List<Track> = repository.read()
    override fun addTrackToHistory(track: Track) = repository.add(track)
    override fun clearHistory() = repository.clear()
}
