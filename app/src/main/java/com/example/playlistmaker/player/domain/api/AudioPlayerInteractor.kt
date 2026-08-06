package com.example.playlistmaker.player.domain.api

interface AudioPlayerInteractor {
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun preparePlayer(
        url: String?,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit,
        onErrorListener: () -> Unit
    )

    fun getCurrentPosition(): Int
    fun playbackControl(onStartUI: () -> Unit, onPauseUI: () -> Unit)
}