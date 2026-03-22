package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository

/**
 * Реализация интерактора для управления плеером.
 * Служит прослойкой между UI и репозиторием.
 */
class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    // Подготовка плеера: передает URL в репозиторий и уведомляет слушателя о готовности
    override fun preparePlayer(url: String, listener: PlayerInteractor.PlayerPreparedListener) {
        repository.prepare(
            url = url,
            onPrepared = {listener.onPrepared()},
            onError = {listener.onError()}
        )
    }

    override fun startPlayer() = repository.start() // Запуск проигрывания

    override fun pausePlayer() = repository.pause() // Пауза

    override fun releasePlayer() = repository.release() // Освобождение ресурсов (при закрытии)

    override fun getCurrentPosition(): Int = repository.getCurrentPosition() // Текущее время трека (мс)

    override fun isPlaying(): Boolean = repository.isPlaying() // Проверка: играет ли музыка сейчас

    // Установка действия, которое выполнится автоматически по окончании трека
    override fun setOnCompletionListener(listener: () -> Unit) {
        repository.setOnCompletionListener(listener)
    }
}