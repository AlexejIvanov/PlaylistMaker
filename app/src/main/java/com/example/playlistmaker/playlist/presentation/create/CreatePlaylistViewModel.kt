package com.example.playlistmaker.playlist.presentation.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    // ИСПРАВЛЕНО: Теперь protected, чтобы EditPlaylistViewModel мог изменять статус
    protected val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> get() = _isSaved

    open fun savePlaylist(
        name: String,
        description: String?,
        imageUriString: String?,
        trackToAdd: Track?
    ) {
        viewModelScope.launch {
            val playlist = Playlist(
                name = name,
                description = description,
                coverFilePath = null,
                trackIds = emptyList(),
                trackCount = 0
            )
            // 1. Создаем плейлист и получаем его ID
            val playlistId = interactor.savePlaylist(playlist, imageUriString)

            // 2. Если передали трек, сохраняем его
            if (trackToAdd != null) {
                val savedPlaylist = playlist.copy(id = playlistId)
                interactor.addTrackToPlaylist(trackToAdd, savedPlaylist)
            }

            // 3. Сообщаем фрагменту, что всё готово
            _isSaved.postValue(true)
        }
    }
}