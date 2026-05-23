package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository

/**
 * Реализация репозитория для управления аудио-плеером через MediaPlayer.
 */
class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {

    // Устанавливает источник аудио и асинхронно готовит плеер к работе
    override fun prepare(url: String, onPrepared: () -> Unit, onError: () -> Unit) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener { onPrepared() } // Вызов колбэка при готовности
            mediaPlayer.setOnCompletionListener(null) // Очищаем старые слушатели
            mediaPlayer.setOnErrorListener { _, _, _ ->
                onError()
                true
            }
            mediaPlayer.prepareAsync()
        } catch (_: Exception) {
            onError()
        }



    }

    override fun start() = mediaPlayer.start() // Начать или продолжить воспроизведение

    override fun pause() = mediaPlayer.pause() // Поставить на паузу

    override fun release() = mediaPlayer.release() // Полное освобождение ресурсов плеера

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition // Текущая позиция (в мс)

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying // Проверка состояния воспроизведения

    // Установка слушателя для события завершения трека
    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener { listener() }
    }
}
