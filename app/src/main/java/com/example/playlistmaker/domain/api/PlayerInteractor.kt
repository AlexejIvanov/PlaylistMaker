package com.example.playlistmaker.domain.api

/**
 * Интерфейс бизнес-логики для управления аудио-плеером.
 */
interface PlayerInteractor {
    // Подготовка плеера к воспроизведению по URL трека
    fun preparePlayer(url: String, listener: PlayerPreparedListener)

    fun startPlayer()   // Запустить воспроизведение
    fun pausePlayer()   // Поставить на паузу
    fun releasePlayer() // Освободить ресурсы плеера (при закрытии экрана)

    fun getCurrentPosition(): Int // Получить текущую позицию проигрывания (в мс)
    fun isPlaying(): Boolean      // Проверка: играет ли плеер сейчас

    // Установка действия, которое выполнится по окончании трека
    fun setOnCompletionListener(listener: () -> Unit)

    // Слушатель для уведомления о том, что плеер успешно загрузил данные
    interface PlayerPreparedListener {
        fun onPrepared()
    }
}