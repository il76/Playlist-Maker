package com.il76.playlistmaker.search.domain.impl

import com.il76.playlistmaker.search.domain.api.TracksInteractor
import com.il76.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(term))
        }
    }
}