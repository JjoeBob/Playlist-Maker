package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.AudioPlayer

class AudioPlayerImpl : AudioPlayer {
    private val audioPlayer = MediaPlayer()

    override fun start() {
        audioPlayer.start()
    }

    override fun pause() {
        audioPlayer.pause()
    }

    override fun release() {
        audioPlayer.release()
    }

    override fun preparePlayer(
        url: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        audioPlayer.setDataSource(url)
        audioPlayer.prepareAsync()
        audioPlayer.setOnPreparedListener { onPreparedListener() }
        audioPlayer.setOnCompletionListener { onCompletionListener() }
    }

    override fun getCurrentPosition(): Int {
        return audioPlayer.currentPosition
    }
}