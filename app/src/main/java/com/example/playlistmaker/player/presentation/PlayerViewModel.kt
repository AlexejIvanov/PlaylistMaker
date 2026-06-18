package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.favorite.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ViewModel для управления состоянием экрана плеера, таймером, избранным и плейлистами.
 */
class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    companion object {
        private const val UPDATE_DELAY_MILLIS = 300L
        private const val DEFAULT_TIMER_VALUE = "00:00"
        private val trackTimeFormatter by lazy {
            SimpleDateFormat("mm:ss", Locale.getDefault())
        }
    }

    private val _state = MutableLiveData<PlayerState>(PlayerState.Default)
    val state: LiveData<PlayerState> get() = _state

    private val _timer = MutableLiveData(DEFAULT_TIMER_VALUE)
    val timer: LiveData<String> get() = _timer

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private var timerJob: Job? = null
    private var currentTrack: Track? = null

    init {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlistsList ->
                _playlists.value = playlistsList // Исправлено на .value
            }
        }
    }

    fun preparePlayer(track: Track) {
        currentTrack = track
        _isFavorite.value = track.isFavorite
        playerInteractor.preparePlayer(
            track.previewUrl,
            object : PlayerInteractor.PlayerPreparedListener {
                override fun onPrepared() {
                    _state.value = PlayerState.Prepared
                }

                override fun onError() {
                    // Оставляем postValue, так как коллбэк плеера может сработать в фоновом потоке
                    _state.postValue(PlayerState.Default)
                }
            },
        )
        playerInteractor.setOnCompletionListener {
            _state.value = PlayerState.Prepared
            _timer.value = DEFAULT_TIMER_VALUE
            timerJob?.cancel()
        }
    }

    fun play() {
        playerInteractor.startPlayer()
        _state.value = PlayerState.Playing
        startTimer()
    }

    fun pause() {
        playerInteractor.pausePlayer()
        _state.value = PlayerState.Paused
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                val position = playerInteractor.getCurrentPosition()
                _timer.value = formatTime(position) // Исправлено на .value
                delay(UPDATE_DELAY_MILLIS)
            }
        }
    }

    private fun formatTime(millis: Int): String {
        return if (millis < 0) {
            DEFAULT_TIMER_VALUE
        } else {
            trackTimeFormatter.format(millis) // Используем оптимизированный форматтер
        }
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return
        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteTrackInteractor.removeTrackFromFavorites(track)
                track.isFavorite = false
            } else {
                favoriteTrackInteractor.addTrackToFavorites(track)
                track.isFavorite = true
            }
            _isFavorite.value = track.isFavorite // Исправлено на .value
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val trackIdLong = track.trackId

        if (playlist.trackIds.contains(trackIdLong)) {
            _toastMessage.value =
                "Трек уже добавлен в плейлист ${playlist.name}" // Исправлено на .value
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(track, playlist)
                _toastMessage.value =
                    "Добавлено в плейлист ${playlist.name}" // Исправлено на .value
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
    }
}