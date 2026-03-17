package com.example.playlistmaker.presentation.search

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val handler: Handler
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val _state = MutableLiveData<SearchScreenState>()
    val state: LiveData<SearchScreenState> get() = _state

    private var searchRunnable: Runnable? = null
    private var isClickAllowed = true

    fun searchDebounce(query: String) {
        if (query.isBlank()) {
            handler.removeCallbacksAndMessages(null)
            showHistory()
            return
        }

        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable { search(query) }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    fun search(query: String) {
        if (query.isBlank()) return

        _state.postValue(SearchScreenState.Loading)

        tracksInteractor.searchTracks(query, object : TracksInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                if (foundTracks != null) {
                    if (foundTracks.isEmpty()) {
                        _state.postValue(SearchScreenState.Empty)
                    } else {
                        _state.postValue(SearchScreenState.Content(foundTracks))
                    }
                } else {
                    _state.postValue(SearchScreenState.Error(errorMessage ?: "Network error"))
                }
            }
        })
    }

    fun showHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNotEmpty()) {
            _state.value = SearchScreenState.History(history)
        } else {
            _state.value = SearchScreenState.Content(emptyList())
        }
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
        if (_state.value is SearchScreenState.History) {
            showHistory()
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _state.value = SearchScreenState.Content(emptyList())
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }
}