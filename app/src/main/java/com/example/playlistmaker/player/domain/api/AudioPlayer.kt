package com.example.playlistmaker.player.domain.api

interface AudioPlayer {
    fun start()
    fun pause()
    fun release()
    fun preparePlayer(
        url: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    )

    fun isPlaying(): Boolean

    fun getCurrentPosition(): Int
}