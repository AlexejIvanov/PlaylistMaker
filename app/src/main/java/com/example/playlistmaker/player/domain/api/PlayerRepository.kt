package com.example.playlistmaker.player.domain.api

/**
 * Интерфейс репозитория для работы с аудио-плеером.
 * Определяет контракт для реализации проигрывателя в слое Data.
 */
interface PlayerRepository {
    // Подготовка плеера: установка источника звука и колбэк при готовности
    fun prepare(url: String, onPrepared: () -> Unit, onError: () -> Unit)

    fun start()             // Запуск воспроизведения
    fun pause()             // Приостановка воспроизведения
    fun release()           // Освобождение ресурсов (удаление плеера)

    fun getCurrentPosition() : Int // Получение текущей секунды трека (мс)
    fun isPlaying() : Boolean      // Состояние: играет трек или нет

    // Установка слушателя на момент окончания трека
    fun setOnCompletionListener(listener: () -> Unit)
}