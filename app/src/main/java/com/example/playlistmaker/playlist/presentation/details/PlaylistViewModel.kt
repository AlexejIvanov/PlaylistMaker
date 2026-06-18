package com.example.playlistmaker.playlist.presentation.details

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistId: Int,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _duration = MutableLiveData<String>()
    val duration: LiveData<String> = _duration

    private val _trackCount = MutableLiveData<Int>()
    val trackCount: LiveData<Int> = _trackCount

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    // LiveData для отслеживания закрытия экрана после удаления плейлиста
    private val _closeScreen = MutableLiveData<Boolean>()
    val closeScreen: LiveData<Boolean> = _closeScreen

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            interactor.getPlaylistById(playlistId).collect { foundPlaylist ->
                _playlist.value = foundPlaylist
                _trackCount.value = foundPlaylist.trackCount

                interactor.getTracksFromPlaylist(foundPlaylist.trackIds).collect { trackList ->
                    val durationSum = trackList.sumOf { it.trackTimeMillis ?: 0L }
                    _duration.value =
                        SimpleDateFormat("mm", Locale.getDefault()).format(durationSum)
                    _tracks.value = trackList.reversed()
                }
            }
        }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            interactor.deleteTrackFromPlaylist(trackId, playlistId)
            loadData()
        }
    }

    // --- ДОБАВЛЕНО: Удаление плейлиста ---
    fun deletePlaylist() {
        viewModelScope.launch {
            interactor.deletePlaylist(playlistId)
            _closeScreen.value = true
        }
    }

    // --- ДОБАВЛЕНО: Генерация строки для "Поделиться" ---
    fun generateShareString(resources: Resources): String {
        val currentPlaylist = playlist.value ?: return ""
        val currentTracks = tracks.value ?: emptyList()

        var shareText = "${currentPlaylist.name}\n"
        if (!currentPlaylist.description.isNullOrEmpty()) {
            shareText += "${currentPlaylist.description}\n"
        }

        val trackCountString = resources.getQuantityString(
            R.plurals.tracks_plural,
            currentPlaylist.trackCount,
            currentPlaylist.trackCount
        )
        shareText += "$trackCountString\n"

        currentTracks.forEachIndexed { index, track ->
            val duration =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            shareText += "${index + 1}. ${track.artistName} - ${track.trackName} ($duration)\n"
        }

        return shareText
    }
}