package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

/**
 * Интерфейс бизнес-логики для поиска треков.
 */
interface TracksInteractor {
    // Выполняет поиск треков и передает результат в TrackConsumer
    fun searchTracks(exception: String, consumer: TrackConsumer)

    /**
     * Интерфейс-коллбэк для получения результатов поиска.
     */
    interface TrackConsumer {
        // Вызывается при получении данных: передает список треков ИЛИ сообщение об ошибке
        fun consume(foundTrack: List<Track>?, errorMassage: String?)
    }
}