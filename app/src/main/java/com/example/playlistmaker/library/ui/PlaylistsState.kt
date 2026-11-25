package com.example.playlistmaker.library.ui

import com.example.playlistmaker.playlist.domain.Playlist

sealed interface PlaylistsState {
    data class Content(val playlists: List<Playlist>) : PlaylistsState
    object Empty : PlaylistsState
}