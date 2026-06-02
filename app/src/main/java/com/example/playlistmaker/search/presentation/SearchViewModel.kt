package com.example.playlistmaker.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorite.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.core.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel для управления состоянием экрана поиска.
 * Обрабатывает поисковые запросы, историю поиска и логику задержек (Debounce).
 */
class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val _state = MutableLiveData<SearchScreenState>()
    val state: LiveData<SearchScreenState> get() = _state

    private var searchJob: Job? = null
    private var isClickAllowed = true

    init {
        showHistory()
    }

    // Debounce клика на корутинах
    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    // Debounce поиска на корутинах
    fun searchDebounce(query: String) {
        if (query.isBlank()) {
            searchJob?.cancel()
            showHistory()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(query)
        }
    }

    fun search(query: String) {
        if (query.isBlank()) return

        _state.value = SearchScreenState.Loading

        viewModelScope.launch {
            tracksInteractor
                .searchTracks(query)
                .collect { pair ->
                    val foundTracks = pair.first
                    val errorMessage = pair.second

                    if (foundTracks != null) {
                        if (foundTracks.isEmpty()) {
                            _state.postValue(SearchScreenState.Empty)
                        } else {
                            _state.postValue(SearchScreenState.Content(foundTracks))
                        }
                    } else {
                        _state.postValue(SearchScreenState.Error(errorMessage ?: "Unknown error"))
                    }
                }
        }
    }

    fun showHistory() {
        viewModelScope.launch {
            val history = searchHistoryInteractor.getHistory()
            val favoriteIds = favoriteTrackInteractor.getFavoriteTracks().first().map { it.trackId }.toSet()
            
            history.forEach { it.isFavorite = favoriteIds.contains(it.trackId) }

            _state.value = if (history.isNotEmpty()) {
                SearchScreenState.History(history)
            } else {
                SearchScreenState.Content(emptyList())
            }
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _state.value = SearchScreenState.Content(emptyList())
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
    }

    // Метод для обновления статуса "Избранное" у текущего состояния (после возвращения с экрана плеера)
    fun updateFavorites() {
        val currentState = _state.value
        if (currentState is SearchScreenState.Content || currentState is SearchScreenState.History) {
            viewModelScope.launch {
                val favoriteIds = favoriteTrackInteractor.getFavoriteTracks().first().map { it.trackId }.toSet()
                
                val tracksToUpdate = when (currentState) {
                    is SearchScreenState.Content -> currentState.tracks
                    is SearchScreenState.History -> currentState.tracks
                    else -> emptyList()
                }

                tracksToUpdate.forEach { it.isFavorite = favoriteIds.contains(it.trackId) }
                _state.postValue(currentState)
            }
        }
    }
}
