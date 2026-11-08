package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.models.AudioPlayerState


interface AudioPlayerInteractor {
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun preparePlayer(url: String?)

    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean

    fun setOnStateChangeListener(onStateChangeListener: (AudioPlayerState) -> Unit)
}