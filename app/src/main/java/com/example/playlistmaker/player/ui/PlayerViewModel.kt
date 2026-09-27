package com.example.playlistmaker.player.ui

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.models.AudioPlayerState
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val timeFormatter: SimpleDateFormat,
    private val handler: Handler
) : ViewModel() {
    private var isInitialized = false

    private val timerRunnable = object : Runnable {
        override fun run() {
            playbackTimeLiveData.postValue(timeFormatter.format(audioPlayerInteractor.getCurrentPosition()))
            handler.postDelayed(this, UPDATE_TIME_DELAY)
        }
    }

    private val playbackTimeLiveData = MutableLiveData("0:00")
    fun observePlaybackTime(): LiveData<String> = playbackTimeLiveData

    private val playerStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Default)
    fun observePlayerState(): LiveData<PlayerScreenState> = playerStateLiveData

    private val trackUILiveData = MutableLiveData<TrackPlayerUI>()
    fun observeTrackUI(): LiveData<TrackPlayerUI> = trackUILiveData

    fun prepare() {
        if (isInitialized) return

        audioPlayerInteractor.setOnStateChangeListener { playerState ->
            val uiState = when (playerState) {
                AudioPlayerState.PREPARED -> {
                    pauseTimer()
                    playbackTimeLiveData.postValue("0:00")
                    PlayerScreenState.Prepared
                }

                AudioPlayerState.DEFAULT, AudioPlayerState.ERROR -> {
                    pauseTimer()
                    PlayerScreenState.Default
                }

                AudioPlayerState.PLAYING -> {
                    startTimer()
                    PlayerScreenState.Playing
                }

                AudioPlayerState.PAUSED -> {
                    pauseTimer()
                    PlayerScreenState.Paused
                }

                AudioPlayerState.COMPLETED -> {
                    pauseTimer()
                    playbackTimeLiveData.postValue("0:00")
                    PlayerScreenState.Completed
                }
            }
            playerStateLiveData.postValue(uiState)
        }

        preparePlayer(track)
        trackUILiveData.postValue(mapToUI(track))

        isInitialized = true
    }

    private fun mapToUI(track: Track): TrackPlayerUI {
        return TrackPlayerUI(
            trackName = track.trackName,
            artistName = track.artistName,
            duration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime),
            albumName = track.collectionName,
            releaseYear = track.releaseDate?.take(4),
            genre = track.primaryGenreName ?: "",
            country = track.country ?: "",
            coverArtworkUrl = track.getCoverArtwork()
        )
    }

    private fun preparePlayer(track: Track) {
        val url = track.previewUrl
        audioPlayerInteractor.preparePlayer(url)
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            PlayerScreenState.Playing -> pausePlayer()
            PlayerScreenState.Prepared, PlayerScreenState.Paused, PlayerScreenState.Completed -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        audioPlayerInteractor.startPlayer()
    }

    private fun startTimer() {
        handler.post(timerRunnable)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
    }

    private fun releasePlayer() {
        audioPlayerInteractor.releasePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(timerRunnable)
        releasePlayer()
    }

    companion object {
        private const val UPDATE_TIME_DELAY = 100L
    }
}