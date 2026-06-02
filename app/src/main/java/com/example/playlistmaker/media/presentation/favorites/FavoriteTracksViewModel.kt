package com.example.playlistmaker.media.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.favorite.domain.db.FavoriteTrackInteractor
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана "Избранные треки".
 * Подписывается на поток данных из БД и обновляет состояние экрана.
 */
class FavoriteTracksViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteState>()
    val state: LiveData<FavoriteState> get() = _state

    init {
        fillData()
    }

    // Получение списка избранных треков
    fun fillData() {
        viewModelScope.launch {
            favoriteTrackInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            _state.postValue(FavoriteState.Empty)
        } else {
            _state.postValue(FavoriteState.Content(tracks))
        }
    }
}
