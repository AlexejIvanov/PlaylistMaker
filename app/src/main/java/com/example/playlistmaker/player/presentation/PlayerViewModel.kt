package com.example.playlistmaker.player.presentation


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.favorite.domain.db.FavoriteTrackInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * ViewModel для управления состоянием экрана плеера и логикой таймера.
 */
class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
) : ViewModel() {

    companion object {
        private const val UPDATE_DELAY_MILLIS = 300L // Частота обновления таймера
        private const val DEFAULT_TIMER_VALUE = "00:00"
    }

    // Состояние плеера для управления UI (кнопки, доступность)
    private val _state = MutableLiveData<PlayerState>(PlayerState.Default)
    val state: LiveData<PlayerState> get() = _state

    // Текущее время воспроизведения в формате "mm:ss"
    private val _timer = MutableLiveData(DEFAULT_TIMER_VALUE)
    val timer: LiveData<String> get() = _timer

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private var timerJob: Job? = null
    private var currentTrack: Track? = null


    // Подготовка плеера: установка URL и слушателей завершения трека
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

    // Запуск воспроизведения и старт обновления таймера
    fun play() {
        playerInteractor.startPlayer()
        _state.value = PlayerState.Playing
        startTimer()
    }

    // Остановка воспроизведения и прекращение обновлений таймера
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
                _timer.value = formatTime(position)
                delay(UPDATE_DELAY_MILLIS)
            }
        }
    }

    private fun formatTime(millis: Int): String {
        return if (millis < 0) {
            DEFAULT_TIMER_VALUE
        } else {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
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
            _isFavorite.postValue(track.isFavorite)
        }
    }

    // Освобождение ресурсов при уничтожении ViewModel (предотвращение утечек)
    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
    }
}
