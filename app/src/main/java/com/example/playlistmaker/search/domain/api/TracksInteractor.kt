package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.core.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс бизнес-логики для поиска треков.
 */
interface TracksInteractor {
    // Выполняет поиск треков и передает результат в TrackConsumer
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>

    /**
     * Интерфейс-коллбэк для получения результатов поиска.
     */
    interface TrackConsumer {
        // Вызывается при получении данных: передает список треков ИЛИ сообщение об ошибке
        fun consume(foundTrack: List<Track>?, errorMassage: String?)
    }
}
