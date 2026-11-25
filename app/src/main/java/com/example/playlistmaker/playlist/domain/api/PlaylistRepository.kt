package com.example.playlistmaker.playlist.domain.api

import com.example.playlistmaker.playlist.domain.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun savePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
}