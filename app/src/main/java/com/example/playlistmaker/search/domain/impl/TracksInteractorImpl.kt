package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.ExecutorService

/**
 * Реализация интерактора для поиска треков.
 * Управляет выполнением поисковых запросов в фоновом потоке.
 */
class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val executor: ExecutorService // Поток из пула для асинхронной работы
) : TracksInteractor {

    override fun searchTracks(exception: String, consumer: TracksInteractor.TrackConsumer) {
        // Выполняем запрос в фоновом потоке, чтобы не блокировать UI
        executor.execute {
            repository.searchTrack(exception) { track, errorMassage ->
                // Возвращаем результат (список треков или ошибку) через consumer
                consumer.consume(track, errorMassage)
            }
        }
    }
}