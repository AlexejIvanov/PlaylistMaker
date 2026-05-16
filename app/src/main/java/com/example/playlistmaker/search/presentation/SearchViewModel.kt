package com.example.playlistmaker.search.presentation

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.core.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel для управления состоянием экрана поиска.
 * Обрабатывает поисковые запросы, историю поиска и логику задержек (Debounce).
 */
class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() { // Handler больше не нужен!

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
                .collect { pair -> // Собираем данные из Flow
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

    // Методы истории остаются почти такими же, но без Handler
    fun showHistory() {
        val history = searchHistoryInteractor.getHistory()
        _state.value = if (history.isNotEmpty()) {
            SearchScreenState.History(history)
        } else {
            SearchScreenState.Content(emptyList())
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _state.value = SearchScreenState.Content(emptyList())
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
        if (_state.value is SearchScreenState.History) {
            showHistory()
        }
    }
}
