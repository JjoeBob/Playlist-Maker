package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(query: String): Flow<List<Track>?> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(query))
        when (response.resultCode) {
            200 -> {
                with(response as TracksSearchResponse) {
                    val tracks = results.map {
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
                    emit(tracks)
                }

            }

            -1 -> {
                emit(null)
            }

            else -> {
                emit(emptyList())
            }
        }
    }

    override fun cancelSearch() {
        networkClient.cancelRequest()
    }
}