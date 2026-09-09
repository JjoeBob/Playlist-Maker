package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(query: String, consumer: TracksConsumer)

    fun interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }

    fun cancelSearch()
}