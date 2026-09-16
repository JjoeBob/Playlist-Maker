package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.AudioPlayer
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.models.AudioPlayerState

class AudioPlayerInteractorImpl(val audioPlayer: AudioPlayer) : AudioPlayerInteractor {
    private var playerState = AudioPlayerState.DEFAULT
    private var onStateChangeListener: ((AudioPlayerState) -> Unit)? = null

    override fun startPlayer() {
        audioPlayer.start()
        updateState(AudioPlayerState.PLAYING)
    }

    override fun pausePlayer() {
        if (playerState == AudioPlayerState.PLAYING) {
            audioPlayer.pause()
            updateState(AudioPlayerState.PAUSED)
        }
    }

    override fun releasePlayer() {
        audioPlayer.release()
        updateState(AudioPlayerState.DEFAULT)
    }

    override fun preparePlayer(
        url: String?
    ) {
        if (url.isNullOrEmpty()) {
            updateState(AudioPlayerState.ERROR)
        } else {
            audioPlayer.preparePlayer(
                url,
                onPreparedListener = {
                    updateState(AudioPlayerState.PREPARED)
                },
                onCompletionListener = {
                    updateState(AudioPlayerState.COMPLETED)
                }
            )
        }
    }

    override fun getCurrentPosition(): Int {
        return audioPlayer.getCurrentPosition()
    }

    override fun setOnStateChangeListener(onStateChangeListener: (AudioPlayerState) -> Unit) {
        this.onStateChangeListener = onStateChangeListener
    }

    private fun updateState(state: AudioPlayerState) {
        playerState = state
        onStateChangeListener?.invoke(playerState)
    }
}