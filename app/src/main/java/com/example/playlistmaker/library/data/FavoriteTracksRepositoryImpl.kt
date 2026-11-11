package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.TrackDbConvertor
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val trackDbConvertor: TrackDbConvertor,
    private val appDatabase: AppDatabase
) : FavoriteTracksRepository {
    override fun favoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun likeTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track).copy(
            addedAt = System.currentTimeMillis()
        )
        appDatabase.trackDao().insertNewTrack(trackEntity)
    }

    override fun checkId(trackId: Long): Flow<Boolean> = flow {
        val isFavorite = appDatabase.trackDao().checkId(trackId)
        emit(isFavorite)
    }

    override suspend fun removeFromFavorites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>) =
        tracks.map { track -> trackDbConvertor.map(track) }
}