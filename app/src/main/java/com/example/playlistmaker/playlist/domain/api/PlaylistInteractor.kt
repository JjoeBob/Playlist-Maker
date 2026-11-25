package com.example.playlistmaker.playlist.domain.api

import com.example.playlistmaker.playlist.domain.Playlist

interface PlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist)
}