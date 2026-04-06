package com.example.playlistmaker.player.presentation

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ViewModel для управления состоянием экрана плеера и логикой таймера.
 */
class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val handler: Handler
) : ViewModel() {

    companion object {
        private const val UPDATE_DELAY_MILLIS = 300L // Частота обновления таймера
        private const val DEFAULT_TIMER_VALUE = "00:00"
    }

    // Состояние плеера для управления UI (кнопки, доступность)
    private val _state = MutableLiveData<PlayerState>(PlayerState.Default)
    val state: LiveData<PlayerState> get() = _state

    // Текущее время воспроизведения в формате "mm:ss"
    private val _timer = MutableLiveData<String>(DEFAULT_TIMER_VALUE)
    val timer: LiveData<String> get() = _timer

    // Задача для периодического обновления таймера пока трек играет
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (_state.value == PlayerState.Playing) {
                val currentPosition = playerInteractor.getCurrentPosition()
                _timer.value = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                handler.postDelayed(this, UPDATE_DELAY_MILLIS)
            }
        }
    }

    // Подготовка плеера: установка URL и слушателей завершения трека
    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url, object : PlayerInteractor.PlayerPreparedListener {
            override fun onPrepared() {
                _state.value = PlayerState.Prepared
            }

            override fun onError() {
                _state.postValue(PlayerState.Default)
            }
        })
        playerInteractor.setOnCompletionListener {
            _state.value = PlayerState.Prepared
            _timer.value = DEFAULT_TIMER_VALUE
            handler.removeCallbacks(timerRunnable)
        }
    }

    // Запуск воспроизведения и старт обновления таймера
    fun play() {
        playerInteractor.startPlayer()
        _state.value = PlayerState.Playing
        handler.post(timerRunnable)
    }

    // Остановка воспроизведения и прекращение обновлений таймера
    fun pause() {
        playerInteractor.pausePlayer()
        _state.value = PlayerState.Paused
        handler.removeCallbacks(timerRunnable)
    }

    // Освобождение ресурсов при уничтожении ViewModel (предотвращение утечек)
    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        handler.removeCallbacks(timerRunnable)
    }
}
