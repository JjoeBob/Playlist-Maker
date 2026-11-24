package com.example.playlistmaker.player.ui

sealed interface TrackState {
    object Favorite: TrackState
    object Default: TrackState
}