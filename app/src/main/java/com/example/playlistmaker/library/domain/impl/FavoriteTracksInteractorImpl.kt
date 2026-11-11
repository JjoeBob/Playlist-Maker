package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {
    override fun favoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.favoriteTracks()
    }

    override suspend fun likeTrack(track: Track) {
        favoriteTracksRepository.likeTrack(track)
    }

    override fun checkId(trackId: Long): Flow<Boolean> {
        return favoriteTracksRepository.checkId(trackId)
    }

    override suspend fun removeFromFavorites(track: Track) {
        favoriteTracksRepository.removeFromFavorites(track)
    }
}