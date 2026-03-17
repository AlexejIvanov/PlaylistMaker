package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.ExecutorService

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val executor: ExecutorService
) : TracksInteractor {

    override fun searchTracks(exception: String, consumer: TracksInteractor.TrackConsumer) {
        executor.execute {
            repository.searchTrack(exception) { track, errorMassage ->
                consumer.consume(track, errorMassage)
            }
        }
    }
}
