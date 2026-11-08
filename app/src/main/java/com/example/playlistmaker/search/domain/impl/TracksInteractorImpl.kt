package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(query: String): Flow<List<Track>?> {
        return repository.searchTracks(query)
    }

    override fun cancelSearch() {
        repository.cancelSearch()
    }
}