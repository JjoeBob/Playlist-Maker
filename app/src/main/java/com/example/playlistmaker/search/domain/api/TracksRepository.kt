package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(query: String) : List<Track>?

    fun cancelSearch()
}