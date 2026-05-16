package com.example.playlistmaker.search.presentation

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.core.models.Track

/**
 * ViewModel для управления состоянием экрана поиска.
 * Обрабатывает поисковые запросы, историю поиска и логику задержек (Debounce).
 */
class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val handler: Handler
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L // Задержка перед началом поиска (мс)
        private const val CLICK_DEBOUNCE_DELAY = 1000L  // Защита от повторных кликов (мс)
    }


    // LiveData для управления состоянием экрана (загрузка, контент, ошибка и т.д.)
    private val _state = MutableLiveData<SearchScreenState>()
    val state: LiveData<SearchScreenState> get() = _state

    private var searchRunnable: Runnable? = null
    private var isClickAllowed = true // Флаг для блокировки кликов
    private var lastClickTime: Long? = null

    init {
        showHistory()
    }

    /**
     * Реализация Debounce: откладывает выполнение поиска на 2 секунды после ввода.
     */
    fun searchDebounce(query: String) {
        if (query.isBlank()) {
            handler.removeCallbacksAndMessages(null)
            showHistory() // Если поле очищено — показываем историю
            return
        }

        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable { search(query) }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    /**
     * Выполнение сетевого запроса через интерактор.
     */
    fun search(query: String) {
        if (query.isBlank()) return

        _state.postValue(SearchScreenState.Loading) // Показываем ProgressBar

        tracksInteractor.searchTracks(query, object : TracksInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                if (foundTracks != null) {
                    if (foundTracks.isEmpty()) {
                        _state.postValue(SearchScreenState.Empty) // Ничего не нашли
                    } else {
                        _state.postValue(SearchScreenState.Content(foundTracks)) // Успех
                    }
                } else {
                    _state.postValue(SearchScreenState.Error(errorMessage ?: "Network error")) // Ошибка сети
                }
            }
        })
    }

    /**
     * Загрузка и отображение истории поиска.
     */
    fun showHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNotEmpty()) {
            _state.value = SearchScreenState.History(history)
        } else {
            _state.value = SearchScreenState.Content(emptyList())
        }
    }

    /**
     * Добавление трека в историю и обновление экрана.
     */
    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
            if(_state.value is SearchScreenState.History) {
                showHistory()
            }
    }

    /**
     * Очистка истории поиска.
     */
    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _state.value = SearchScreenState.Content(emptyList())
    }

    /**
     * Метод для ограничения частоты кликов (защита от открытия двух экранов плеера).
     */
    fun clickDebounce(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime == null || currentTime - lastClickTime!! >= CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime
            return true
        }
        return false
    }

    /**
     * Очистка Handler при уничтожении ViewModel для предотвращения утечек памяти.
     */
    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }
}
