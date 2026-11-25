package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.playlist.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.playlist.domain.Playlist
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository

class PlaylistRepositoryImpl(
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val appDatabase: AppDatabase
) : PlaylistRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }
}
