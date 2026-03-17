package com.example.playlistmaker.presentation.player

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val handler: Handler
) : ViewModel() {

    companion object {
        private const val UPDATE_DELAY_MILLIS = 300L
        private const val DEFAULT_TIMER_VALUE = "00:00"
    }

    private val _state = MutableLiveData<PlayerState>(PlayerState.Default)
    val state: LiveData<PlayerState> get() = _state

    private val _timer = MutableLiveData<String>(DEFAULT_TIMER_VALUE)
    val timer: LiveData<String> get() = _timer

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (_state.value == PlayerState.Playing) {
                val currentPosition = playerInteractor.getCurrentPosition()
                _timer.value = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                handler.postDelayed(this, UPDATE_DELAY_MILLIS) // Используем константу
            }
        }
    }

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url, object : PlayerInteractor.PlayerPreparedListener {
            override fun onPrepared() {
                _state.value = PlayerState.Prepared
            }
        })
        playerInteractor.setOnCompletionListener {
            _state.value = PlayerState.Prepared
            _timer.value = DEFAULT_TIMER_VALUE // Используем константу
            handler.removeCallbacks(timerRunnable)
        }
    }

    fun play() {
        playerInteractor.startPlayer()
        _state.value = PlayerState.Playing
        handler.post(timerRunnable)
    }

    fun pause() {
        playerInteractor.pausePlayer()
        _state.value = PlayerState.Paused
        handler.removeCallbacks(timerRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        handler.removeCallbacks(timerRunnable)
    }
}
