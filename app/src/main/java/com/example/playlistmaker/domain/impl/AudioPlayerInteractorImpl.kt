package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.AudioPlayer
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.models.PlayerState

class AudioPlayerInteractorImpl(val audioPlayer: AudioPlayer) : AudioPlayerInteractor {
    private var playerState = PlayerState.DEFAULT

    override fun startPlayer() {
        audioPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pausePlayer() {
        audioPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun releasePlayer() {
        audioPlayer.release()
        playerState = PlayerState.DEFAULT
    }

    override fun preparePlayer(
        url: String?,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit,
        onErrorListener: () -> Unit
    ) {
        if (url.isNullOrEmpty()) {
            onErrorListener()
        } else {
            audioPlayer.preparePlayer(
                url,
                onPreparedListener = {
                    playerState = PlayerState.PREPARED
                    onPreparedListener()
                },
                onCompletionListener = {
                    playerState = PlayerState.PREPARED
                    onCompletionListener()
                }
            )
        }
    }

    override fun getCurrentPosition(): Int {
        return audioPlayer.getCurrentPosition()
    }

    override fun playbackControl(onStartUI: () -> Unit, onPauseUI: () -> Unit) {
        when (playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
                onPauseUI()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
                onStartUI()
            }

            PlayerState.DEFAULT -> {}
        }
    }
}