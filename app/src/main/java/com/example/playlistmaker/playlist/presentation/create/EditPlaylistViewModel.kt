package com.example.playlistmaker.playlist.presentation.create

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val interactor: PlaylistInteractor
) : CreatePlaylistViewModel(interactor) {

    var currentPlaylistId: Int = -1

    // ИСПРАВЛЕНО: Добавлено override и полное совпадение параметров
    override fun savePlaylist(
        name: String,
        description: String?,
        imageUriString: String?,
        trackToAdd: Track? // Этот параметр здесь не используется, но нужен для совпадения сигнатур
    ) {
        viewModelScope.launch {
            // Вызываем метод обновления
            interactor.updatePlaylistDetails(currentPlaylistId, name, description, imageUriString)

            // Сообщаем фрагменту об успешном сохранении, чтобы он закрылся
            _isSaved.postValue(true)
        }
    }
}