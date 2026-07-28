package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(query: String): List<Track>? {
        val response = networkClient.doRequest(TracksSearchRequest(query))
        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
                Track(
                    trackId = it.trackId,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = it.trackTime,
                    artworkUrl = it.artworkUrl,
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }
        } else if (response.resultCode == -1) {
            return null
        } else {
            return emptyList()
        }
    }

    override fun cancelSearch() {
        networkClient.cancelRequest()
    }
}