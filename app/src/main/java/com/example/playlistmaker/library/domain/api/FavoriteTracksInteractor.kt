package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    fun favoriteTracks(): Flow<List<Track>>
    suspend fun likeTrack(track: Track)
    fun checkId(trackId: Long): Flow<Boolean>
    suspend fun removeFromFavorites(track: Track)
}