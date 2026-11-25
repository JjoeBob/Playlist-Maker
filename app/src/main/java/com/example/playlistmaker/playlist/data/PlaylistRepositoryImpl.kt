package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.playlist.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.Playlist
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val appDatabase: AppDatabase
) : PlaylistRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromTrackEntity(playlists))
    }

    private fun convertFromTrackEntity(playlists: List<PlaylistEntity>) =
        playlists.map { playlist -> playlistDbConvertor.map(playlist) }
}
