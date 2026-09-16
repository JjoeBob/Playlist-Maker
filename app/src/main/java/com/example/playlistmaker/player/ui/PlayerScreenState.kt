package com.example.playlistmaker.player.ui

sealed interface PlayerScreenState {
    data object Default : PlayerScreenState
    data object Prepared : PlayerScreenState
    data object Playing : PlayerScreenState
    data object Paused : PlayerScreenState
    data object Completed: PlayerScreenState
}
