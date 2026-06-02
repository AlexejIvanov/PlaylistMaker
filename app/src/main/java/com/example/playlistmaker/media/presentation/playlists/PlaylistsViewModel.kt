package com.example.playlistmaker.media.presentation.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>()
    val state: LiveData<PlaylistsState> get() = _state

    fun loadPlaylists() {
        viewModelScope.launch {
            interactor.getPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    _state.postValue(PlaylistsState.Empty)
                } else {
                    _state.postValue(PlaylistsState.Content(playlists))
                }
            }
        }
    }
}
