package com.example.playlistmaker.domain.api

interface AudioPlayer {
    fun start()
    fun pause()
    fun release()
    fun preparePlayer(
        url: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    )

    fun getCurrentPosition(): Int
}