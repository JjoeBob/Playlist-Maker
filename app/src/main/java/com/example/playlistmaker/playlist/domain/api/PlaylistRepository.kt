package com.example.playlistmaker.playlist.domain.api

import com.example.playlistmaker.playlist.domain.Playlist

interface PlaylistRepository {
    suspend fun savePlaylist(playlist: Playlist)
}